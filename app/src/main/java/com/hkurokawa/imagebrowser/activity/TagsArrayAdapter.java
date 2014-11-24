package com.hkurokawa.imagebrowser.activity;

import com.hkurokawa.imagebrowser.R;
import com.hkurokawa.imagebrowser.data.ImageResource;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class TagsArrayAdapter extends ArrayAdapter<String> {
   private final ImageResource imageResource;

   public TagsArrayAdapter(Context context, int resource, ImageResource imageResource) {
      super(context, resource);
      this.imageResource = imageResource;
      this.refreshTags();
   }

   public void refreshTags() {
      final List<String> tags = this.imageResource.listTags();
      
      this.clear();
      // The tag for "All images" must be in the top
      this.add(this.getContext().getResources().getString(R.string.tag_item_all));
      this.addAll(tags);
      
      this.notifyDataSetChanged();
   }

   @SuppressWarnings("static-method")
   public int getTagAllPosition() {
      return 0;
   }
}
