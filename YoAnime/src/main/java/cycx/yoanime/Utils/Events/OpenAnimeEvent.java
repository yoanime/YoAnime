package cycx.yoanime.Utils.Events;

import cycx.yoanime.Models.Anime;

public class OpenAnimeEvent {
    public final Anime anime;

    public OpenAnimeEvent(Anime anime) {
        this.anime = anime;
    }

}
