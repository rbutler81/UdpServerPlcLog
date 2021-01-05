package com.cimcorp.plc.logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BitmapHandler {

    String path;
    List<Bitmap> bitmaps = new ArrayList<>();

    public BitmapHandler(String path) {
        this.path = path;
    }

    public void parseLine(String s) {

        if (s.contains("START")) {
            int lb = s.indexOf("[");
            int rb = s.indexOf("]");
            int comma = s.indexOf(",",lb);
            String xRes = s.substring(lb+1,comma);
            String yRes = s.substring(comma+1,rb);
            String filename = s.substring(rb+1);

            bitmaps.add(new Bitmap(path + filename, Integer.parseInt(xRes), Integer.parseInt(yRes)));

        } else if (s.contains("STOP")) {
            int lb = s.indexOf("[");
            int rb = s.indexOf("]");
            String filename = s.substring(lb+1,rb);

            BufferedImage img = null;
            for (Bitmap b : bitmaps) {
                if (b.getFilename().equals(path + filename)) {
                    img = b.complete();
                    break;
                }
            }

            // write to disk
            Path p = Paths.get(path);
            if (!Files.exists(p)) {
                try {
                    Files.createDirectories(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            File file = new File(path + filename + ".bmp");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (file.exists()) {
                if (file.canWrite()) {

                    try {
                        ImageIO.write(img, "BMP", file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < bitmaps.size(); i++) {
                        if (bitmaps.get(i).getFilename().equals(path + filename)) {
                            bitmaps.remove(i);
                            break;
                        }
                    }
                }
            }

        }  else {
                int lb = s.indexOf("[");
                int rb = s.indexOf("]");
                int header = s.indexOf(",BMP,") + 5;
                String filename = s.substring(header,lb);
                String lineNumber = s.substring(lb+1,rb);
                String data = s.substring(rb+1);

                for (Bitmap b : bitmaps) {
                    if (b.getFilename().equals(path + filename)) {
                        b.addLine(Integer.parseInt(lineNumber), data);
                    }
                }
        }

    }
}
