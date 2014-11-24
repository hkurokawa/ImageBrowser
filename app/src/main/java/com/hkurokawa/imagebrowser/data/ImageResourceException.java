package com.hkurokawa.imagebrowser.data;

public class ImageResourceException extends RuntimeException {
   private static final long serialVersionUID = 6569290908915420068L;

   public ImageResourceException(final String msg) {
      super(msg);
   }
   
   public ImageResourceException(final String msg, final Throwable cause) {
      super(msg, cause);
   }
}
