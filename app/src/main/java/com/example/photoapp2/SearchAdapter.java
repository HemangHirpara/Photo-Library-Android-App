package com.example.photoapp2;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

// The standard text view adapter only seems to search from the beginning of whole words
// so we've had to write this whole class to make it possible to search
// for parts of the arbitrary string we want
public class SearchAdapter extends BaseAdapter implements Filterable {

    private ArrayList<Photo>originalData = null;
    private ArrayList<Photo>filteredData = null;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();

    public SearchAdapter(Context context, List<Photo> data) {
        this.filteredData = (ArrayList<Photo>) data;
        this.originalData = (ArrayList<Photo>) data;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return filteredData.size();
    }

    public Object getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.photo_lv_item, null);
            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.caption);
            holder.imageView = (ImageView) convertView.findViewById(R.id.thumbnail);
            convertView.setTag(holder);
            // Bind the data efficiently with the holder.
            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // If weren't re-ordering this you could rely on what you set last time
        holder.txtTitle.setText(filteredData.get(position).getCaption());
        holder.imageView.setImageURI(Uri.parse(filteredData.get(position).getPhotoFile()));
        return convertView;
    }

    static class ViewHolder {
        public TextView txtTitle;
        public ImageView imageView;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString;
            Boolean filterByPerson = constraint.toString().startsWith("PERSON");
            FilterResults results = new FilterResults();

            final ArrayList<Photo> list = originalData;

            int count = list.size();
            final ArrayList<Photo> nlist = new ArrayList<Photo>(count);

            for (int i = 0; i < count; i++) {
                if(filterByPerson) {
                     filterString = constraint.toString().toLowerCase().substring(6);
                    if (list.get(i).getPersonTag().toLowerCase().startsWith(filterString))
                        nlist.add(list.get(i));
                }
                else{
                    filterString = constraint.toString().toLowerCase().substring(8);
                    if (list.get(i).getLocationTag().toLowerCase().startsWith(filterString))
                        nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Photo>) results.values;
            notifyDataSetChanged();
        }

    }
}