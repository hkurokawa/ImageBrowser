package com.hkurokawa.imagebrowser.data;

public class DuplicateTagNameException extends ImageResourceException {
   private static final long serialVersionUID = -8074232467040067897L;
   private final String tagName;

   public DuplicateTagNameException(final String tagName) {
      super(getErrorMessage(tagName));
      this.tagName = tagName;
   }

   public DuplicateTagNameException(final String tagName, final Throwable cause) {
      super(getErrorMessage(tagName), cause);
      this.tagName = tagName;
   }

   public String getTagName() {
      return this.tagName;
   }

   private static String getErrorMessage(final String tagName) {
      return "The specified tag name already exists: [" + tagName + "].";
   }
}
