package cycx.yoanime.Models.Hummingbird;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "episodes_watched",
    "last_watched",
    "updated_at",
    "rewatched_times",
    "notes",
    "notes_present",
    "status",
    "private",
    "rewatching",
    "anime",
    "rating"
})
public class HBLibraryEntry {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("episodes_watched")
    private Integer episodesWatched;
    @JsonProperty("last_watched")
    private String lastWatched;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("rewatched_times")
    private Integer rewatchedTimes;
    @JsonProperty("notes")
    private Object notes;
    @JsonProperty("notes_present")
    private Object notesPresent;
    @JsonProperty("status")
    private String status;
    @JsonProperty("private")
    private Boolean _private;
    @JsonProperty("rewatching")
    private Boolean rewatching;
    @JsonProperty("anime")
    private HBAnime anime;
    @JsonProperty("rating")
    private HBRating rating;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The id
     */
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The episodesWatched
     */
    @JsonProperty("episodes_watched")
    public Integer getEpisodesWatched() {
        return episodesWatched;
    }

    /**
     * 
     * @param episodesWatched
     *     The episodes_watched
     */
    @JsonProperty("episodes_watched")
    public void setEpisodesWatched(Integer episodesWatched) {
        this.episodesWatched = episodesWatched;
    }

    /**
     * 
     * @return
     *     The lastWatched
     */
    @JsonProperty("last_watched")
    public String getLastWatched() {
        return lastWatched;
    }

    /**
     * 
     * @param lastWatched
     *     The last_watched
     */
    @JsonProperty("last_watched")
    public void setLastWatched(String lastWatched) {
        this.lastWatched = lastWatched;
    }

    /**
     * 
     * @return
     *     The updatedAt
     */
    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 
     * @param updatedAt
     *     The updated_at
     */
    @JsonProperty("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 
     * @return
     *     The rewatchedTimes
     */
    @JsonProperty("rewatched_times")
    public Integer getRewatchedTimes() {
        return rewatchedTimes;
    }

    /**
     * 
     * @param rewatchedTimes
     *     The rewatched_times
     */
    @JsonProperty("rewatched_times")
    public void setRewatchedTimes(Integer rewatchedTimes) {
        this.rewatchedTimes = rewatchedTimes;
    }

    /**
     * 
     * @return
     *     The notes
     */
    @JsonProperty("notes")
    public Object getNotes() {
        return notes;
    }

    /**
     * 
     * @param notes
     *     The notes
     */
    @JsonProperty("notes")
    public void setNotes(Object notes) {
        this.notes = notes;
    }

    /**
     * 
     * @return
     *     The notesPresent
     */
    @JsonProperty("notes_present")
    public Object getNotesPresent() {
        return notesPresent;
    }

    /**
     * 
     * @param notesPresent
     *     The notes_present
     */
    @JsonProperty("notes_present")
    public void setNotesPresent(Object notesPresent) {
        this.notesPresent = notesPresent;
    }

    /**
     * 
     * @return
     *     The status
     */
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 
     * @return
     *     The _private
     */
    @JsonProperty("private")
    public Boolean getPrivate() {
        return _private;
    }

    /**
     * 
     * @param _private
     *     The private
     */
    @JsonProperty("private")
    public void setPrivate(Boolean _private) {
        this._private = _private;
    }

    /**
     * 
     * @return
     *     The rewatching
     */
    @JsonProperty("rewatching")
    public Boolean getRewatching() {
        return rewatching;
    }

    /**
     * 
     * @param rewatching
     *     The rewatching
     */
    @JsonProperty("rewatching")
    public void setRewatching(Boolean rewatching) {
        this.rewatching = rewatching;
    }

    /**
     * 
     * @return
     *     The anime
     */
    @JsonProperty("anime")
    public HBAnime getAnime() {
        return anime;
    }

    /**
     * 
     * @param anime
     *     The anime
     */
    @JsonProperty("anime")
    public void setAnime(HBAnime anime) {
        this.anime = anime;
    }

    /**
     * 
     * @return
     *     The rating
     */
    @JsonProperty("rating")
    public HBRating getRating() {
        return rating;
    }

    /**
     * 
     * @param rating
     *     The rating
     */
    @JsonProperty("rating")
    public void setRating(HBRating rating) {
        this.rating = rating;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
