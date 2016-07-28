package cycx.yoanime;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import cycx.yoanime.Models.Anime;
import cycx.yoanime.Utils.Events.CircularTransform;
import cycx.yoanime.Utils.Events.DrawerAdapterClickListener;

public class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final DrawerAdapterClickListener adapterClickListener;
    private static final int VIEW_TYPE_HEADER = -1;
    private static final int VIEW_TYPE_ITEM = 0;

    private final Context context;
    private List<Anime> favourites;
    private String hbUsername, avatar, cover;
    private int avatarLength;

    public DrawerAdapter (Context context, DrawerAdapterClickListener adapterClickListener, List<Anime> favourites) {
        this.context = context;
        this.adapterClickListener = adapterClickListener;
        this.favourites = favourites;
    }



    public void setFavourites (List<Anime> favourites) {
        this.favourites = favourites;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.drawer_header, parent, false);

            View drawerUserButton = view.findViewById(R.id.user_selectable);
            drawerUserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterClickListener.onUserItemClicked();
                }
            });


            return new HeaderViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drawer_fave_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

            headerViewHolder.usernameTextView.setText(hbUsername);

            RequestCreator avatarRequest;
            if (avatar != null) {
                avatarRequest = Picasso.with(context)
                        .load(avatar)
                        .transform(new CircularTransform(avatarLength, 0));
            } else {
               // avatarRequest = Picasso.with(context)
                      //  .load(R.drawable.user_stock_avatar);
            }

            RequestCreator coverRequest;
            if (cover != null) {
                coverRequest = Picasso.with(context)
                        .load(cover);
            } else {
                coverRequest = Picasso.with(context)
                        .load(R.drawable.drawer_cover);
            }

            //avatarRequest
                  //  .fit()
                   // .centerCrop()
                  //  .into(headerViewHolder.userAvatarImageView);

            coverRequest
                    .fit()
                    .centerCrop()
                    .into(headerViewHolder.userCoverImageView);
        }
        else {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            int majorColour = getItem(position).getMajorColour();
            if (majorColour == 0) {
                itemViewHolder.iconView.setColorFilter(MainApplication.RED_ACCENT_RGB, PorterDuff.Mode.SRC_ATOP);
            } else {
                itemViewHolder.iconView.setColorFilter(majorColour, PorterDuff.Mode.SRC_ATOP);
            }
            itemViewHolder.titleView.setText(getItem(position).getTitle());
            itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapterClickListener.onCLick(getItem(position), position, view);
                }
            });
        }
    }

    private Anime getItem (int position) {
        return favourites.get(position - 1);
    }

    @Override
    public int getItemCount() {
        return favourites.size() + 1;
    }

    public void setAvatarLength(int avatarLength) {
        this.avatarLength = avatarLength;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout itemView;
        public TextView titleView;
        public ImageView iconView;

        public ItemViewHolder(View v) {
            super(v);
            itemView = (RelativeLayout) v.findViewById(R.id.drawer_favourite_item);
            titleView = (TextView) v.findViewById(R.id.favourite_title_view);
            iconView = (ImageView) v.findViewById(R.id.favourite_icon);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView;
        public ImageView userAvatarImageView;
        public ImageView userCoverImageView;

        public HeaderViewHolder(View v) {
            super(v);

            usernameTextView = (TextView) v.findViewById(R.id.drawer_user_name);
            //userAvatarImageView = (ImageView) v.findViewById(R.id.drawer_user_avatar);
            userCoverImageView = (ImageView) v.findViewById(R.id.drawer_user_cover);
        }
    }

    public void updateUserData (String hbUsername, String avatar, String cover) {
        this.hbUsername = hbUsername;
        this.avatar = avatar;
        this.cover = cover;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_ITEM;
    }
}
