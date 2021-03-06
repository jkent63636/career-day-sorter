package com.atcs.career.resources;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

import com.atcs.career.program.ErrorManager;

//Edward Fominykh
//Program Description
//Nov 16, 2018
public class FontManager
{
    
    private static Font f;
    
    public static Font finalFont(float size) {
        if (f == null)
      	  f = new Font("Gill Sans", Font.PLAIN, (int)size);
//            f =  readOpenSans(size);
        return f.deriveFont(size);
    }
    
    /**
     * use only once
     * @param size
     * @return
     */
    private static Font readFont(float size) {
        Font x = null;
        try {
            x = Font
                    .createFont(Font.TRUETYPE_FONT,
                            ClassLoader.getSystemClassLoader().getResourceAsStream("com/atcs/career/resources/OpenSans-Light.ttf"))
                    .deriveFont(size);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            // register the font
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
                    ClassLoader.getSystemClassLoader().getResourceAsStream("com/atcs/career/resources/OpenSans-Light.ttf")));
        } catch (FontFormatException | IOException e) {
            ErrorManager.processException(e, "failed to create font", false, false);
        }
        return x;
    }
}
