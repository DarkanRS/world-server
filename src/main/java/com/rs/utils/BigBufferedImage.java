// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.utils;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class BigBufferedImage extends BufferedImage {

	private static final String TMP_DIR = "D:\\temp";
	public static final int MAX_PIXELS_IN_MEMORY =  1024 * 1024;

	public static BufferedImage create(int width, int height, int imageType) {
		if (width * height <= MAX_PIXELS_IN_MEMORY)
			return new BufferedImage(width, height, imageType);
		try {
			final File tempDir = new File(TMP_DIR);
			System.out.println("Width: " + width +", " + height);
			return createBigBufferedImage(tempDir, width, height, imageType);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static BufferedImage create(File inputFile, int imageType) throws IOException {
		try (ImageInputStream stream = ImageIO.createImageInputStream(inputFile);) {
			Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
			if (readers.hasNext())
				try {
					ImageReader reader = readers.next();
					reader.setInput(stream, true, true);
					int width = reader.getWidth(reader.getMinIndex());
					int height = reader.getHeight(reader.getMinIndex());
					BufferedImage image = create(width, height, imageType);
					int cores = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
					int block = Math.min(MAX_PIXELS_IN_MEMORY / cores / width, (int) (Math.ceil(height / (double) cores)));
					ExecutorService generalExecutor = Executors.newFixedThreadPool(cores);
					List<Callable<ImagePartLoader>> partLoaders = new ArrayList<>();
					for (int y = 0; y < height; y += block)
						partLoaders.add(new ImagePartLoader(
								y, width, Math.min(block, height - y), inputFile, image));
					generalExecutor.invokeAll(partLoaders);
					generalExecutor.shutdown();
					return image;
				} catch (InterruptedException ex) {
					Logger.getLogger(BigBufferedImage.class.getName()).log(Level.SEVERE, null, ex);
				}
		}
		return null;
	}

	private static BufferedImage createBigBufferedImage(File tempDir, int width, int height, int imageType)
			throws FileNotFoundException, IOException {
		FileDataBuffer buffer = new FileDataBuffer(tempDir, width * height, 4);
		ColorModel colorModel = null;
		BandedSampleModel sampleModel = null;
		sampleModel = switch (imageType) {
		case TYPE_INT_RGB -> {
			colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
					new int[]{8, 8, 8, 0},
					false,
					false,
					Transparency.TRANSLUCENT,
					DataBuffer.TYPE_BYTE);
			yield new BandedSampleModel(DataBuffer.TYPE_BYTE, width, height, 3);
		}
		case TYPE_INT_ARGB -> {
			colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
					new int[]{8, 8, 8, 8},
					true,
					false,
					Transparency.TRANSLUCENT,
					DataBuffer.TYPE_BYTE);
			yield new BandedSampleModel(DataBuffer.TYPE_BYTE, width, height, 4);
		}
		default -> throw new IllegalArgumentException("Unsupported image type: " + imageType);
		};
		SimpleRaster raster = new SimpleRaster(sampleModel, buffer, new Point(0, 0));
		BigBufferedImage image = new BigBufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
		return image;
	}

	private static class ImagePartLoader implements Callable<ImagePartLoader> {

		private final int y;
		private final BufferedImage image;
		private final Rectangle region;
		private final File file;

		public ImagePartLoader(int y, int width, int height, File file, BufferedImage image) {
			this.y = y;
			this.image = image;
			this.file = file;
			region = new Rectangle(0, y, width, height);
		}

		@Override
		public ImagePartLoader call() throws Exception {
			Thread.currentThread().setPriority((Thread.MIN_PRIORITY + Thread.NORM_PRIORITY) / 2);
			try (ImageInputStream stream = ImageIO.createImageInputStream(file);) {
				Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
				if (readers.hasNext()) {
					ImageReader reader = readers.next();
					reader.setInput(stream, true, true);
					ImageReadParam param = reader.getDefaultReadParam();
					param.setSourceRegion(region);
					BufferedImage part = reader.read(0, param);
					Raster source = part.getRaster();
					WritableRaster target = image.getRaster();
					target.setRect(0, y, source);
				}
			}
			return ImagePartLoader.this;
		}
	}

	private BigBufferedImage(ColorModel cm, SimpleRaster raster, boolean isRasterPremultiplied, Hashtable<?, ?> properties) {
		super(cm, raster, isRasterPremultiplied, properties);
	}

	public void dispose() {
		((SimpleRaster) getRaster()).dispose();
	}

	public static void dispose(RenderedImage image) {
		if (image instanceof BigBufferedImage bbi)
			bbi.dispose();
	}

	private static class SimpleRaster extends WritableRaster {

		public SimpleRaster(SampleModel sampleModel, FileDataBuffer dataBuffer, Point origin) {
			super(sampleModel, dataBuffer, origin);
		}

		public void dispose() {
			((FileDataBuffer) getDataBuffer()).dispose();
		}

	}

	private static final class FileDataBufferDeleterHook extends Thread {

		static {
			Runtime.getRuntime().addShutdownHook(new FileDataBufferDeleterHook());
		}

		private static final HashSet<FileDataBuffer> undisposedBuffers = new HashSet<>();

		@Override
		public void run() {
			final FileDataBuffer[] buffers = undisposedBuffers.toArray(new FileDataBuffer[0]);
			for (FileDataBuffer b : buffers)
				b.disposeNow();
		}
	}

	private static class FileDataBuffer extends DataBuffer {

		private final String id = "buffer-" + System.currentTimeMillis() + "-" + ((int) (Math.random() * 1000));
		private File dir;
		private String path;
		private File[] files;
		private RandomAccessFile[] accessFiles;
		private MappedByteBuffer[] buffer;

		public FileDataBuffer(File dir, int size) throws FileNotFoundException, IOException {
			super(TYPE_BYTE, size);
			this.dir = dir;
			init();
		}

		public FileDataBuffer(File dir, int size, int numBanks) throws FileNotFoundException, IOException {
			super(TYPE_BYTE, size, numBanks);
			this.dir = dir;
			init();
		}

		private void init() throws FileNotFoundException, IOException {
			FileDataBufferDeleterHook.undisposedBuffers.add(this);
			if (dir == null)
				dir = new File(".");
			if (!dir.exists())
				throw new RuntimeException("FileDataBuffer constructor parameter dir does not exist: " + dir);
			if (!dir.isDirectory())
				throw new RuntimeException("FileDataBuffer constructor parameter dir is not a directory: " + dir);
			path = dir.getPath() + "/" + id;
			File subDir = new File(path);
			subDir.mkdir();
			buffer = new MappedByteBuffer[banks];
			accessFiles = new RandomAccessFile[banks];
			files = new File[banks];
			for (int i = 0; i < banks; i++) {
				File file = files[i] = new File(path + "/bank" + i + ".dat");
				final RandomAccessFile randomAccessFile = accessFiles[i] = new RandomAccessFile(file, "rw");
				buffer[i] = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, getSize());
			}
		}

		@Override
		public int getElem(int bank, int i) {
			return buffer[bank].get(i) & 0xff;
		}

		@Override
		public void setElem(int bank, int i, int val) {
			buffer[bank].put(i, (byte) val);
		}

		private void disposeNow() {
			final MappedByteBuffer[] disposedBuffer = buffer;
			buffer = null;
			disposeNow(disposedBuffer);
		}

		public void dispose() {
			final MappedByteBuffer[] disposedBuffer = buffer;
			buffer = null;
			new Thread() {
				@Override
				public void run() {
					disposeNow(disposedBuffer);
				}
			}.start();
		}

		private void disposeNow(final MappedByteBuffer[] disposedBuffer) {
			FileDataBufferDeleterHook.undisposedBuffers.remove(this);
			if (disposedBuffer != null)
				for (MappedByteBuffer b : disposedBuffer)
					b.clear();
			if (accessFiles != null) {
				for (RandomAccessFile file : accessFiles)
					try {
						file.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				accessFiles = null;
			}
			if (files != null) {
				for (File file : files)
					file.delete();
				files = null;
			}
			if (path != null) {
				new File(path).delete();
				path = null;
			}
		}

	}
}
