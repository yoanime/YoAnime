package cycx.yoanime.Utils.Events;

import android.support.annotation.Nullable;
import android.view.View;

import cycx.yoanime.Models.Anime;

public interface DrawerAdapterClickListener {

    void onCLick(Anime item, @Nullable Integer position, @Nullable View view);

    void onUserItemClicked();

}
