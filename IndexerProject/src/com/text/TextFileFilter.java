package com.text;

import java.io.File;
import java.io.FileFilter;
import java.util.Locale;

public class TextFileFilter implements FileFilter {

    @Override
    public boolean accept(File pathname) {
        if(pathname.getName().toLowerCase().endsWith(".txt") ||
                pathname.getName().toLowerCase().endsWith(".pdf") ||
                     pathname.getName().toLowerCase().endsWith(".docx") ||
                         pathname.getName().toLowerCase().endsWith(".doc"))
            return true;
        else
            return false;

    }
}
