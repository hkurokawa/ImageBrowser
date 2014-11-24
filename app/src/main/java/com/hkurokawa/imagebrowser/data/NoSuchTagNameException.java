package com.hkurokawa.imagebrowser.data;

public class NoSuchTagNameException extends ImageResourceException {
   private static final long serialVersionUID = 7913962149693931742L;
   private final String tagName;

   public NoSuchTagNameException(final String tagName) {
      super("The specified tag does not exist: [" + tagName + "].");
      this.tagName = tagName;
   }
   
   public String getTagName() {
      return this.tagName;
   }
}
