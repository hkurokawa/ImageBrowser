package com.hkurokawa.imagebrowser.data.local;

interface Constants {
   public static final String TABLE_IMAGES = "images";
   public static final String COLUMN_IMAGES_URI = "_uri";

   public static final String TABLE_IMAGES_TAGS = "images_tags";
   public static final String COLUMN_IMAGES_TAGS_IMAGE_URI = "_image_uri";
   public static final String COLUMN_IMAGES_TAGS_TAG_NAME = "_tag_name";

   public static final String TABLE_TAGS = "tags";
   public static final String COLUMN_TAGS_NAME = "_name";
}
