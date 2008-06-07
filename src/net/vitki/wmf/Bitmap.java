package net.vitki.wmf;

import java.awt.*;
import java.awt.image.*;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;

public class Bitmap  {

    // nominal header
    public int  headerSize;
    public int  width;
    public int  height;
    public int  numColorPlanes;
    public int  bitsPerPixel;
    public int  compression;
    public int  bitmapSize;
    public int  xPelsPerMeter;
    public int  yPelsPerMeter;
    public int  clrUsed;
    public int  clrImportant;

    // standalone header
    public boolean standalone;
    public String  bmpMagic;
    public int     fileSize;
    public int     reserved1;
    public int     imageOffset;

    public Image   image;

    public static final int ALPHA_WHITE  = -1;
    public static final int ALPHA_BLACK  = -2;
    public static final int ALPHA_SOURCE = -3;

    public Bitmap()  {
        width = height = 0;
        numColorPlanes = bitsPerPixel = 0;
        compression = 0;
        bitmapSize = 0;
        xPelsPerMeter = yPelsPerMeter = 0;
        clrUsed = clrImportant = 0;
        image = null;
        standalone = false;
        bmpMagic = "";
        fileSize = 0;
        imageOffset = 0;
    }

    public void readHeader (RecordReader rd)  throws IOException {
        headerSize = rd.readLong();       // 0,1
        if (headerSize != 40)
            throw new IOException ("unsupported header size "+headerSize);
        width  = rd.readLong();           // 2,3
        height = rd.readLong();           // 4,5
        numColorPlanes = rd.readWord();   // 6
        bitsPerPixel = rd.readWord();     // 7
        compression  = rd.readLong();     // 8,9
        bitmapSize = rd.readLong();        // 10,11
        xPelsPerMeter = rd.readLong();    // 12,13
        yPelsPerMeter = rd.readLong();    // 14,15
        clrUsed = rd.readLong();          // 16,17
        clrImportant = rd.readLong();     // 18,19
    }

    public void read (RecordReader rd, boolean standalone, int bytes, int alpha)
        throws IOException
    {
        this.standalone = standalone;
        if (standalone)  {
            byte[] ba = rd.readBytes (2, false);
            bmpMagic = new String (ba, 0, 2);
            if (!"BM".equals(bmpMagic))
                throw new IOException ("this is not BMP");
            fileSize = rd.readLong();
            reserved1 = rd.readLong();
            imageOffset = rd.readLong();
            standalone = true;
        }
        readHeader (rd);
        if (bytes < 0)  {
            if (standalone)
                bytes = fileSize - rd.getPosition();
            else
                bytes = rd.restBytes();
        }
        image = createImage(rd.readBytes(bytes), alpha);
    }

    public void read (InputStream is)  throws IOException  {
        RecordReader rd = new RecordReader (is, null);
        read (rd, true, -1, ALPHA_SOURCE);
    }

    public Image createImage(byte[] byteData, int alpha)
        throws IOException
    {
        int[]   palette = null;
        int[]   pixels  = null;
        int     stride  = 0;
        int     offset  = 0;
        int     size    = 0;
        int     i, j, n, r, g, b, a, poff, boff;
        byte    d;

        if (compression != 0)
            throw new IOException ("unsupported RLE compression "+compression);
        // palette
        switch (bitsPerPixel)
        {
            case 1:
            case 4:
            case 8:
                if (clrUsed <= 0)
                    clrUsed = (1 << bitsPerPixel);
                palette = new int[clrUsed];
                for (i =0; i < clrUsed; i++)  {
                    b = (int)byteData[offset+0] & 255;
                    g = (int)byteData[offset+1] & 255;
                    r = (int)byteData[offset+2] & 255;
                    switch (alpha)  {
                        case ALPHA_WHITE:
                            a = (r==255 && g==255 && b==255) ? 0 : 255;
                            break;
                        case ALPHA_BLACK:
                            a = (r==0 && g==0 && b==0) ? 0 : 255;
                            break;
                        case ALPHA_SOURCE:
                            a = (int)byteData[offset+3] & 255;
                            break;
                        default:
                            a = alpha & 255;
                            break;
                    }
                    offset += 4;
                    palette[i] = ((a << 24) + (r << 16) + (g << 8) + b);
                }
                break;
            case 24:
                break;
            default:
                throw new IOException ("unsupported bpp "+bitsPerPixel);
        }

        switch (bitsPerPixel)
        {
            case 1:
                n = width / 8;
                if (n * 8 < width)   n++;
                while (n % 4 != 0)   n++;
                stride = n * 8;
                size = height * stride;
                pixels = new int [size];
                for (i = height -1; i >= 0; i--)    {
                    poff = i*stride;
                    boff = offset;
                    for (j = 0; j < n; j++)  {
                        d = byteData[boff];
                        pixels[poff+0] = palette[d >>7 & 1];
                        pixels[poff+1] = palette[d >>6 & 1];
                        pixels[poff+2] = palette[d >>5 & 1];
                        pixels[poff+3] = palette[d >>4 & 1];
                        pixels[poff+4] = palette[d >>3 & 1];
                        pixels[poff+5] = palette[d >>2 & 1];
                        pixels[poff+6] = palette[d >>1 & 1];
                        pixels[poff+7] = palette[d & 1];
                        poff += 8;
                        boff ++;
                    }
                    offset += n;
                }
                break;
            case 4:
                stride = width / 2;
                if (stride * 2 < width)   stride++;
                while (stride % 4 != 0)   stride++;
                size = height * stride;
                pixels = new int [size];
                for (i = height -1; i >= 0; i--)  {
                    poff = i*stride;
                    boff = offset;
                    for (j = 0; j < width; j++)  {
                        d = byteData[boff];
                        pixels[poff+0]= palette[d >>4 & 15];
                        pixels[poff+1]= palette[d & 15];
                        poff += 2;
                        boff ++;
                    }
                    offset += stride;
                }
                break;
            case 8:
                stride = width;
                while (stride % 4 != 0)   stride++;
                size = height * stride;
                pixels = new int [size];
                for (i = height -1; i >= 0; i--)   {
                    poff = i*stride;
                    boff = offset;
                    for (j = 0; j < width; j++)  {
                        d = byteData[boff];
                        pixels[poff]= palette[d < 0 ? d+256 : d];
                        poff ++;
                        boff ++;
                    }
                    offset += stride;
                }
                break;
            case 24:
                stride = n = width * 3;
                while (stride % 4 != 0)    stride++;
                size = height * stride;
                pixels = new int [size];
                a = (alpha < 0 ? 255 : alpha & 255) << 24;
                for (i = height -1; i >= 0; i--)  {
                    boff = offset;
                    poff = i*stride;
                    for (j = 0; j < width; j++)  {
                        b = (int)byteData[boff+0] & 255;
                        g = (int)byteData[boff+1] & 255;
                        r = (int)byteData[boff+2] & 255;
                        switch (alpha)  {
                            case ALPHA_WHITE:
                                a = (r==255 && g==255 && b==255) ? 0 : 255;
                                break;
                            case ALPHA_BLACK:
                                a = (r==0 && g==0 && b==0) ? 0 : 255;
                                break;
                            case ALPHA_SOURCE:
                                a = 255;
                                break;
                            default:
                                a = alpha & 255;
                                break;
                        }
                        pixels[poff] = (a << 24) | (r << 16) | (g << 8) | b;
                        poff ++;
                        boff += 3;
                    }
                    offset += stride;
                }
                break;
        }
        return createImage (pixels, width, height, stride, alpha);
    }

    private Image createImage (int[] pixels, int width, int height,
                               int stride, int alpha)
    {
        PackedColorModel cmodel = (PackedColorModel)ColorModel.getRGBdefault();
        DataBuffer dbuf = new DataBufferInt (pixels, height * stride, 0);
        WritableRaster wraster = Raster.createWritableRaster
            (new SinglePixelPackedSampleModel (dbuf.getDataType(),
                                               width, height, stride,
                                               cmodel.getMasks()),
             dbuf, null);
        return new BufferedImage (cmodel, wraster, false, null);
    }

    public String toString()  {
        final String C = ",";
        return ("bmp{"+
                 (standalone ? (
                   "["+
                     '"'+bmpMagic+'"'+C+
                     "fileSize="+fileSize+C+
                     "imageOff="+imageOffset+
                   "]"+C
                 ) : "")+
                 "hdrsz="+headerSize+C+
                 "bmpsz="+bitmapSize+C+
                 "dim=("+width+C+height+")"+C+
                 "planes="+numColorPlanes+C+
                 "bpp="+bitsPerPixel+C+
                 "comp="+compression+C+
                 "ppm=("+xPelsPerMeter+C+yPelsPerMeter+")"+C+
                 "clr=("+clrUsed+C+clrImportant+")"+
                "}");
    }

    public Canvas createCanvas ()  {
        return new ImageCanvas(image, width, height);
    }

    class ImageCanvas extends Canvas {
        private Image image;
        private int   w, h;
        public ImageCanvas (Image image, int w, int h)  {
            this.w = width;
            this.h = height;
            this.image = image;
            System.err.println("w="+w+",h="+h+",iw="+image.getWidth(null)+",ih="+image.getHeight(null));
        }
        public Dimension getPreferredSize()  { return new Dimension (w, h); }
        public Dimension getMinimumSize()  { return new Dimension (w, h); }
        public void paint (Graphics g)  { g.drawImage (image, 0, 0, null); }

        static final long serialVersionUID = 0x1aec0002; 
    }

    public static void main (String[] args)  throws Exception {
        Bitmap bmp = new Bitmap();
        bmp.read(new FileInputStream(args[0]));
        System.err.println (bmp);
        Frame frame = new Frame (args[0]);
        frame.add("North", bmp.createCanvas());
        frame.pack();
        frame.setVisible(true);
    }

}
