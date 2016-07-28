package cycx.yoanime.Utils;

import android.support.annotation.Nullable;
import android.view.View;

public interface AdapterClickListener<M> {

    void onCLick (M item, @Nullable Integer position, @Nullable View view);

    void onLongClick (M item, @Nullable Integer position);

}
