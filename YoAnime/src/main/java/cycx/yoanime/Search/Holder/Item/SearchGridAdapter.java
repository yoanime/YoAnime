package cycx.yoanime.Search.Holder.Item;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cycx.yoanime.Models.Anime;
import cycx.yoanime.R;
import cycx.yoanime.Search.Holder.SearchHolderFragment;

public class SearchGridAdapter extends RecyclerView.Adapter<SearchGridAdapter.ViewHolder> {
    private Context context;
    private SearchFragment searchFragment;

    public SearchGridAdapter(SearchFragment searchFragment) {
        this.searchFragment = searchFragment;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView titleView;
        public View view;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.image_view);
            titleView = (TextView) v.findViewById(R.id.title_view);
            view = v.findViewById(R.id.selectable);
        }
    }

    @Override
    public SearchGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        context = parent.getContext();

        View v = LayoutInflater.from(context)
                .inflate(R.layout.search_grid_card, parent, false);

        return  new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        Anime anime = getItem(position);

        Picasso.with(context)
                .load(anime.getImageUrl())
             //   .error(R.drawable.error_stock)
                .fit()
                //.memoryPolicy(MemoryPolicy.NO_STORE)
                .centerCrop()
                .into(viewHolder.imageView);

        viewHolder.titleView.setText(anime.getTitle());

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchFragment.onCLick(getItem(position), null, viewHolder.imageView);
            }
        });

        //setTransitionName(viewHolder, String.valueOf(position));
    }
/*
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setTransitionName (final ViewHolder viewHolder, String uniqueIdentifier) {
        searchFragment.transitionName = SearchFragment.POSTER_TRANSITION_BASE_NAME + uniqueIdentifier;
        viewHolder.imageView.setTransitionName(searchFragment.transitionName);
    }
*/

    private List<Anime> searchResults () {
        return SearchHolderFragment.searchResultsCache.get(searchFragment.getPresenter().providerType);
    }

    private Anime getItem (int position) {
        return searchResults().get(position);
    }

    @Override
    public int getItemCount() {
        return searchResults().size();
    }
}
