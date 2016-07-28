package cycx.yoanime.Models;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

public class SharedElementTransitionBundle {
    public final View sharedElement;
    public final String baseName;
    public final String name;
    public final Bundle bundle;

    public SharedElementTransitionBundle(@NonNull View sharedElement, @NonNull String baseName, @NonNull String name, @NonNull Bundle bundle) {
        this.sharedElement = sharedElement;
        this.baseName = baseName;
        this.name = name;
        this.bundle = bundle;
    }

}
