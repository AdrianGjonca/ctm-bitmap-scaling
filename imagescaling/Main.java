package imagescaling;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Main {

	static BufferedImage in = null;
	static BufferedImage out = null;
	public static void main(String[] args) {
		String a_ = "/home/adri/Pictures/in.png";
		String b_ = "/home/adri/Pictures/out.png";
		
		if(args.length >= 2) {
			a_ = args[0];
			b_ = args[1];
		}
		System.out.print("[File] Loading input image");
		try {
		    in = ImageIO.read(new File(a_));
		    System.out.print(" [Done]\n");
		} catch (IOException e) {
			System.err.print(" [Error]\n");
			System.exit(0);
		}
		
		System.out.println("Generating scaled image");
		out = new BufferedImage(in.getWidth() * 2,in.getHeight() * 2,BufferedImage.TYPE_INT_RGB);
		System.out.println("	Plot know pixels");
		for(int x = 0; x < in.getWidth(); x++) {
			for(int y = 0; y < in.getHeight(); y++) {
				int rgb = in.getRGB(x, y);
				float r = (rgb & 0xFF0000) / 256 / 256;
				float g = (rgb & 0x00FF00) / 256;
				float b = rgb & 0x0000FF;
				
				setRGB(x*2, y*2, (int)r, (int)g, (int)b);				
			}
		}
		
		System.out.println("	Predict sandwitched pixels");
		for(int x = 1; x < out.getWidth(); x+=2) {
			for(int y = 0; y < out.getHeight(); y+=2) {
				int rgb = 0;
				float r = 0;
				float g = 0;
				float b = 0;
				float n = 1;
				rgb = out.getRGB(x-1, y);
				r += (rgb & 0xFF0000) / 256 / 256;
				g += (rgb & 0x00FF00) / 256;
				b += rgb & 0x0000FF;
				
				if(x + 1 < out.getWidth()) {
					rgb = out.getRGB(x+1, y);
					r += (rgb & 0xFF0000) / 256 / 256;
					g += (rgb & 0x00FF00) / 256;
					b += rgb & 0x0000FF;
					n += 1;
				}
				r /= n;
				g /= n;
				b /= n;
				
				//setRGB(x,y, (int)r,  (int)g,  (int)b);
				out.setRGB(x, y, getClosestMatch(x,y,(int)r,(int)g,(int)b));
			}
			
		}
		
		System.out.println("	Predict empty rows pixels");
		for(int x = 1; x < out.getWidth(); x++) {
			for(int y = 1; y < out.getHeight(); y+=2) {
				int rgb = 0;
				float r = 0;
				float g = 0;
				float b = 0;
				float n = 0;
				rgb = out.getRGB(x-1, y-1);
				r += ((rgb & 0xFF0000) / 256 / 256) * 0.7071f;
				g += ((rgb & 0x00FF00) / 256) * 0.7071f;
				b += (rgb & 0x0000FF) * 0.7071f;
				n += 0.7071f;
				if(y+1<out.getHeight()) {
					rgb = out.getRGB(x-1, y+1);
					r += ((rgb & 0xFF0000) / 256 / 256) * 0.7071f;
					g += ((rgb & 0x00FF00) / 256) * 0.7071f;
					b += (rgb & 0x0000FF) * 0.7071f;
					n += 0.7071f;
				}
				if (x<out.getWidth()) {
					rgb = out.getRGB(x, y-1);
					r += ((rgb & 0xFF0000) / 256 / 256) * 1;
					g += ((rgb & 0x00FF00) / 256) * 1;
					b += (rgb & 0x0000FF) * 1;
					n += 1;
					if(y+1<out.getHeight()) {
						rgb = out.getRGB(x, y+1);
						r += ((rgb & 0xFF0000) / 256 / 256) * 1;
						g += ((rgb & 0x00FF00) / 256) * 1;
						b += (rgb & 0x0000FF) * 1;
						n += 1;
					}
				}
				if (x+1<out.getWidth()) {
					rgb = out.getRGB(x+1, y-1);
					r += ((rgb & 0xFF0000) / 256 / 256) * 0.7071f;
					g += ((rgb & 0x00FF00) / 256) * 0.7071f;
					b += (rgb & 0x0000FF) * 0.7071f;
					n += 0.7071f;
					if(y+1<out.getHeight()) {
						rgb = out.getRGB(x+1, y+1);
						r += ((rgb & 0xFF0000) / 256 / 256) * 0.7071f;
						g += ((rgb & 0x00FF00) / 256) * 0.7071f;
						b += (rgb & 0x0000FF) * 0.7071f;
						n += 0.7071f;
					}
				}
				r /= n;
				g /= n;
				b /= n;
				//setRGB(x,y, (int)r,  (int)g,  (int)b);
				out.setRGB(x, y, getClosestMatch2(x,y,(int)r,(int)g,(int)b));
			}
		}
		
		System.out.println("	Cleanup");
		for(int y = 1; y < out.getHeight(); y += 2) {
			out.setRGB(0, y, out.getRGB(0, y-1));
		}
		System.out.print("[Task] Saving output image");
		try {
		    ImageIO.write(out, "png", new File(b_));
		    System.out.println(" [Done]");
		} catch (IOException e) {
			System.err.println(" [Error]");
			System.exit(0);
		}
	}
	
	public static void setRGB(int x, int y, int r, int g, int b) {
		out.setRGB(x, y, r*256*256 + g*256 + b);	
	}
	
	public static int getClosestMatch2(int x, int y, int r, int g, int b) {
		int rgb = 0;
		int bestScore = 100000000;
		int score = 100000;
		int r2 = 0;
		int g2 = 0;
		int b2 = 0;
		int r3 = 0;
		int g3 = 0;
		int b3 = 0;
		rgb = out.getRGB(x-1, y-1);
		r2 += (rgb & 0xFF0000) / 256 / 256;
		g2 += (rgb & 0x00FF00) / 256;
		b2 += rgb & 0x0000FF;
		bestScore = (r2 - r)*(r2 - r) + (g2 - g)*(g2 - g) + (b2 - b)*(b2 - b);
		
		if(y+1<out.getHeight()) {
			rgb = out.getRGB(x-1, y+1);
			r3 += ((rgb & 0xFF0000) / 256 / 256);
			g3 += ((rgb & 0x00FF00) / 256);
			b3 += (rgb & 0x0000FF);
			score = (r3 - r)*(r3 - r) + (g3 - g)*(g3 - g) + (b3 - b)*(b3 - b);
			if(score < bestScore) {
				bestScore = score;
				r2 = r3;
				g2 = g3;
				b2 = b3;
			}
		}
		if (x<out.getWidth()) {
			rgb = out.getRGB(x, y-1);
			r3 += ((rgb & 0xFF0000) / 256 / 256);
			g3 += ((rgb & 0x00FF00) / 256);
			b3 += (rgb & 0x0000FF);
			score = (r3 - r)*(r3 - r) + (g3 - g)*(g3 - g) + (b3 - b)*(b3 - b);
			if(score < bestScore) {
				bestScore = score;
				r2 = r3;
				g2 = g3;
				b2 = b3;
			}
			if(y+1<out.getHeight()) {
				rgb = out.getRGB(x, y+1);
				r3 += ((rgb & 0xFF0000) / 256 / 256);
				g3 += ((rgb & 0x00FF00) / 256);
				b3 += (rgb & 0x0000FF);
				score = (r3 - r)*(r3 - r) + (g3 - g)*(g3 - g) + (b3 - b)*(b3 - b);
				if(score < bestScore) {
					bestScore = score;
					r2 = r3;
					g2 = g3;
					b2 = b3;
				}
			}
		}
		if (x+1<out.getWidth()) {
			rgb = out.getRGB(x+1, y-1);
			r3 += ((rgb & 0xFF0000) / 256 / 256);
			g3 += ((rgb & 0x00FF00) / 256);
			b3 += (rgb & 0x0000FF);
			score = (r3 - r)*(r3 - r) + (g3 - g)*(g3 - g) + (b3 - b)*(b3 - b);
			if(score < bestScore) {
				bestScore = score;
				r2 = r3;
				g2 = g3;
				b2 = b3;
			}
			if(y+1<out.getHeight()) {
				rgb = out.getRGB(x+1, y+1);
				r3 += ((rgb & 0xFF0000) / 256 / 256);
				g3 += ((rgb & 0x00FF00) / 256);
				b3 += (rgb & 0x0000FF);
				score = (r3 - r)*(r3 - r) + (g3 - g)*(g3 - g) + (b3 - b)*(b3 - b);
				if(score < bestScore) {
					bestScore = score;
					r2 = r3;
					g2 = g3;
					b2 = b3;
				}
			}
		}
		
		return r2*256*256 + g2*256 + b2;
	}
	
	public static int getClosestMatch(int x, int y, int r, int g, int b) {
		int rgb = 0;
		int bestScore = 100000000;
		int score = 100000;
		int r2 = 0;
		int g2 = 0;
		int b2 = 0;
		int r3 = 0;
		int g3 = 0;
		int b3 = 0;
		rgb = out.getRGB(x-1, y);
		r2 += (rgb & 0xFF0000) / 256 / 256;
		g2 += (rgb & 0x00FF00) / 256;
		b2 += rgb & 0x0000FF;
		bestScore = (r2 - r)*(r2 - r) + (g2 - g)*(g2 - g) + (b2 - b)*(b2 - b);
		
		if(y + 2 < out.getHeight()) {
			rgb = out.getRGB(x-1, y+2);
			r3 = ((rgb & 0xFF0000) / 256 / 256);
			g3 = ((rgb & 0x00FF00) / 256);
			b3 = (rgb & 0x0000FF);
			score = (r3 - r)*(r3 - r) + (g3 - g)*(g3 - g) + (b3 - b)*(b3 - b);
			if(score < bestScore) {
				bestScore = score;
				r2 = r3;
				g2 = g3;
				b2 = b3;
			}
		}
		if(y - 2 >= 0) {
			rgb = out.getRGB(x-1, y-2);
			r3 = ((rgb & 0xFF0000) / 256 / 256);
			g3 = ((rgb & 0x00FF00) / 256);
			b3 = (rgb & 0x0000FF);
			score = (r3 - r)*(r3 - r) + (g3 - g)*(g3 - g) + (b3 - b)*(b3 - b);
			if(score < bestScore) {
				bestScore = score;
				r2 = r3;
				g2 = g3;
				b2 = b3;
			}
		}
		
		if(x + 1 < out.getWidth()) {
			rgb = out.getRGB(x+1, y);
			r3 = (rgb & 0xFF0000) / 256 / 256;
			g3 = (rgb & 0x00FF00) / 256;
			b3 = rgb & 0x0000FF;
			score = (r3 - r)*(r3 - r) + (g3 - g)*(g3 - g) + (b3 - b)*(b3 - b);
			if(score < bestScore) {
				bestScore = score;
				r2 = r3;
				g2 = g3;
				b2 = b3;
			}
			if(y + 2 < out.getHeight()) {
				rgb = out.getRGB(x+1, y+2);
				r3 = ((rgb & 0xFF0000) / 256 / 256);
				g3 = ((rgb & 0x00FF00) / 256);
				b3 = (rgb & 0x0000FF);
				score = (r3 - r)*(r3 - r) + (g3 - g)*(g3 - g) + (b3 - b)*(b3 - b);
				if(score < bestScore) {
					bestScore = score;
					r2 = r3;
					g2 = g3;
					b2 = b3;
				}
			}
			if(y - 2 >= 0) {
				rgb = out.getRGB(x+1, y-2);
				r3 = ((rgb & 0xFF0000) / 256 / 256);
				g3 = ((rgb & 0x00FF00) / 256);
				b3 = (rgb & 0x0000FF);
				score = (r3 - r)*(r3 - r) + (g3 - g)*(g3 - g) + (b3 - b)*(b3 - b);
				if(score < bestScore) {
					bestScore = score;
					r2 = r3;
					g2 = g3;
					b2 = b3;
				}
			}
		}
		
		return r2*256*256 + g2*256 + b2;
	}

}
