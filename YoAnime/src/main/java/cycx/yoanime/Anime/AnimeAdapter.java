package cycx.yoanime.Anime;

import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cycx.yoanime.Models.Anime;
import cycx.yoanime.Models.Episode;
import cycx.yoanime.R;
import cycx.yoanime.Utils.PaletteTransform;

public class AnimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_FOOTER = 2;

    private final PaletteTransform paletteTransform;
    private List<Episode> episodes;
    private final AnimeFragment animeFragment;
    private final int unwatchedColour;
    private final int watchedColour;
    private boolean isInFavourites;
    //private String transitionName;

    public AnimeAdapter(List<Episode> episodes, AnimeFragment animeFragment, int unwatchedColour, int watchedColour) {
        this.episodes = episodes;
        this.animeFragment = animeFragment;
        this.unwatchedColour = unwatchedColour;
        this.watchedColour = watchedColour;
        paletteTransform = new PaletteTransform();
    }

    public void setAnime (List<Episode> episodes, boolean isInFavourites) {
        this.clear();
        this.episodes = episodes;
        this.isInFavourites = isInFavourites;
        this.notifyDataSetChanged();
    }

    public void clear() {
        this.episodes = new ArrayList<>(0);
        this.notifyDataSetChanged();
    }

    public static class EpisodeViewHolder extends RecyclerView.ViewHolder{
        public TextView titleView;
        public EpisodeViewHolder(View v) {
            super(v);
            titleView = (TextView) v.findViewById(R.id.episode_title_view);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public ImageView posterImageView;
        public TextView descView;
        public TextView genresView;
        public TextView alternateTitleView;
        public TextView dateView;
        public TextView statusView;
        public FloatingActionButton favouriteFab;

        public HeaderViewHolder(View v) {
            super(v);
            posterImageView = (ImageView) v.findViewById(R.id.anime_image_view);
            descView = (TextView) v.findViewById(R.id.anime_desc_view);
            genresView = (TextView) v.findViewById(R.id.anime_genres_view);
            alternateTitleView = (TextView) v.findViewById(R.id.anime_alternate_title_view);
            dateView = (TextView) v.findViewById(R.id.anime_date_view);
            statusView = (TextView) v.findViewById(R.id.anime_status_view);
            favouriteFab = (FloatingActionButton) v.findViewById(R.id.favourite_fab);
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                AnimeAdapter.HeaderViewHolder headerViewHolder = new HeaderViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.yoanime_header, parent, false));

                headerViewHolder.favouriteFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isInFavourites = !isInFavourites;
                        animeFragment.getPresenter().onFavouriteCheckedChanged(isInFavourites);
                        headerViewHolder.favouriteFab.setImageDrawable(favouriteIcon());
                    }
                });

                headerViewHolder.posterImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        animeFragment.showImageDialog();
                    }
                });

                return headerViewHolder;

            case VIEW_TYPE_ITEM:
                return new EpisodeViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.episode, parent, false));

            case VIEW_TYPE_FOOTER:
                return new RecyclerView.ViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.footer, parent, false)) {
                };
        }
        throw new IllegalStateException("Unacceptable view type.");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (animeFragment.getPresenter().lastAnime != null) { // lazy fix for a null pointer
            if (viewHolder instanceof HeaderViewHolder) {
                Anime anime = animeFragment.getPresenter().lastAnime;
                 HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;

                /*
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    headerViewHolder.posterImageView.setTransitionName(transitionName);
                */

                 Picasso.with(animeFragment.getActivity())
                         .load(anime.getImageUrl())
                         .error(R.drawable.placeholder)
                         .fit()
                         .centerCrop()
                         .transform(paletteTransform)
                         .into(headerViewHolder.posterImageView, new Callback.EmptyCallback() {
                             @Override
                             public void onSuccess() {
                                 if (animeFragment.getPresenter() != null) { // lazy fix for nulls pointers when the presenter has already been destroyed
                                     animeFragment.getPresenter().setMajorColour(paletteTransform.getPallete());
                                 }
                             }
                         });

                headerViewHolder.genresView.setText(anime.getGenresString());
                headerViewHolder.descView.setText(anime.getDesc());
                headerViewHolder.alternateTitleView.setText(anime.getAlternateTitle());
                headerViewHolder.dateView.setText(anime.getDate());
                headerViewHolder.statusView.setText(anime.getStatus());
                headerViewHolder.favouriteFab.setImageDrawable(favouriteIcon());

            } else if (viewHolder instanceof EpisodeViewHolder) {
                EpisodeViewHolder episodeViewHolder = (EpisodeViewHolder) viewHolder;
                final int actualPosition = position - 1;
                episodeViewHolder.titleView.setText(episodes.get(actualPosition).getTitle());

                if (episodes.get(actualPosition).isWatched()) {
                    episodeViewHolder.titleView.setTextColor(this.watchedColour);
                } else {
                    episodeViewHolder.titleView.setTextColor(unwatchedColour);
                }

                episodeViewHolder.titleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        animeFragment.onCLick(episodes.get(actualPosition), actualPosition, view);
                    }
                });

                episodeViewHolder.titleView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        animeFragment.onLongClick(episodes.get(actualPosition), actualPosition);
                        return false;
                    }
                });
            }
        } else {
            animeFragment.getPresenter().postError(new Throwable("No anime to display."));
        }
    }
/*
    public void setTransitionName (final String transitionName) {
        this.transitionName = transitionName;
    }
*/

    @Override
    public int getItemCount() {
        return episodes.size() + 2;
    }

    public void setWatched (int position) {
        episodes.set(position, episodes.get(position).setWatched(true));
        this.notifyItemChanged(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == VIEW_TYPE_HEADER) {
            return VIEW_TYPE_HEADER;
        } else if (position < getItemCount() - 1) {
            return VIEW_TYPE_ITEM;
        }
        return VIEW_TYPE_FOOTER;
    }

    public Episode getItemAtPosition (int position) {
        if (episodes != null) {
            return episodes.get(position);
        }
        return null;
    }

    private Drawable favouriteIcon () {
        if (isInFavourites) {
            return ResourcesCompat.getDrawable(animeFragment.getResources(), R.drawable.ic_favorite, null);
        } else {
            return ResourcesCompat.getDrawable(animeFragment.getResources(), R.drawable.ic_favorite_icon, null);
        }
    }

    public void setFabChecked (boolean isInFavourites) {
        this.isInFavourites = isInFavourites;
        notifyDataSetChanged();
    }

}
