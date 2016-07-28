package cycx.yoanime.Models.Hummingbird;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "mal_id",
    "slug",
    "status",
    "url",
    "title",
    "alternate_title",
    "episode_count",
    "episode_length",
    "cover_image",
    "synopsis",
    "show_type",
    "started_airing",
    "finished_airing",
    "community_rating",
    "age_rating",
    "genres"
})
public class HBAnime {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("mal_id")
    private Integer malId;
    @JsonProperty("slug")
    private String slug;
    @JsonProperty("status")
    private String status;
    @JsonProperty("url")
    private String url;
    @JsonProperty("title")
    private String title;
    @JsonProperty("alternate_title")
    private String alternateTitle;
    @JsonProperty("episode_count")
    private Integer episodeCount;
    @JsonProperty("episode_length")
    private Integer episodeLength;
    @JsonProperty("cover_image")
    private String coverImage;
    @JsonProperty("synopsis")
    private String synopsis;
    @JsonProperty("show_type")
    private String showType;
    @JsonProperty("started_airing")
    private String startedAiring;
    @JsonProperty("finished_airing")
    private String finishedAiring;
    @JsonProperty("community_rating")
    private Double communityRating;
    @JsonProperty("age_rating")
    private String ageRating;
    @JsonProperty("genres")
    private List<HBGenre> genres = new ArrayList<HBGenre>();
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
     *     The malId
     */
    @JsonProperty("mal_id")
    public Integer getMalId() {
        return malId;
    }

    /**
     * 
     * @param malId
     *     The mal_id
     */
    @JsonProperty("mal_id")
    public void setMalId(Integer malId) {
        this.malId = malId;
    }

    /**
     * 
     * @return
     *     The slug
     */
    @JsonProperty("slug")
    public String getSlug() {
        return slug;
    }

    /**
     * 
     * @param slug
     *     The slug
     */
    @JsonProperty("slug")
    public void setSlug(String slug) {
        this.slug = slug;
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
     *     The url
     */
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     *     The url
     */
    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 
     * @return
     *     The title
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The alternateTitle
     */
    @JsonProperty("alternate_title")
    public String getAlternateTitle() {
        return alternateTitle;
    }

    /**
     * 
     * @param alternateTitle
     *     The alternate_title
     */
    @JsonProperty("alternate_title")
    public void setAlternateTitle(String alternateTitle) {
        this.alternateTitle = alternateTitle;
    }

    /**
     * 
     * @return
     *     The episodeCount
     */
    @JsonProperty("episode_count")
    public Integer getEpisodeCount() {
        return episodeCount;
    }

    /**
     * 
     * @param episodeCount
     *     The episode_count
     */
    @JsonProperty("episode_count")
    public void setEpisodeCount(Integer episodeCount) {
        this.episodeCount = episodeCount;
    }

    /**
     * 
     * @return
     *     The episodeLength
     */
    @JsonProperty("episode_length")
    public Integer getEpisodeLength() {
        return episodeLength;
    }

    /**
     * 
     * @param episodeLength
     *     The episode_length
     */
    @JsonProperty("episode_length")
    public void setEpisodeLength(Integer episodeLength) {
        this.episodeLength = episodeLength;
    }

    /**
     * 
     * @return
     *     The coverImage
     */
    @JsonProperty("cover_image")
    public String getCoverImage() {
        return coverImage;
    }

    /**
     * 
     * @param coverImage
     *     The cover_image
     */
    @JsonProperty("cover_image")
    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    /**
     * 
     * @return
     *     The synopsis
     */
    @JsonProperty("synopsis")
    public String getSynopsis() {
        return synopsis;
    }

    /**
     * 
     * @param synopsis
     *     The synopsis
     */
    @JsonProperty("synopsis")
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    /**
     * 
     * @return
     *     The showType
     */
    @JsonProperty("show_type")
    public String getShowType() {
        return showType;
    }

    /**
     * 
     * @param showType
     *     The show_type
     */
    @JsonProperty("show_type")
    public void setShowType(String showType) {
        this.showType = showType;
    }

    /**
     * 
     * @return
     *     The startedAiring
     */
    @JsonProperty("started_airing")
    public String getStartedAiring() {
        return startedAiring;
    }

    /**
     * 
     * @param startedAiring
     *     The started_airing
     */
    @JsonProperty("started_airing")
    public void setStartedAiring(String startedAiring) {
        this.startedAiring = startedAiring;
    }

    /**
     * 
     * @return
     *     The finishedAiring
     */
    @JsonProperty("finished_airing")
    public String getFinishedAiring() {
        return finishedAiring;
    }

    /**
     * 
     * @param finishedAiring
     *     The finished_airing
     */
    @JsonProperty("finished_airing")
    public void setFinishedAiring(String finishedAiring) {
        this.finishedAiring = finishedAiring;
    }

    /**
     * 
     * @return
     *     The communityRating
     */
    @JsonProperty("community_rating")
    public Double getCommunityRating() {
        return communityRating;
    }

    /**
     * 
     * @param communityRating
     *     The community_rating
     */
    @JsonProperty("community_rating")
    public void setCommunityRating(Double communityRating) {
        this.communityRating = communityRating;
    }

    /**
     * 
     * @return
     *     The ageRating
     */
    @JsonProperty("age_rating")
    public String getAgeRating() {
        return ageRating;
    }

    /**
     * 
     * @param ageRating
     *     The age_rating
     */
    @JsonProperty("age_rating")
    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    /**
     * 
     * @return
     *     The genres
     */
    @JsonProperty("genres")
    public List<HBGenre> getGenres() {
        return genres;
    }

    /**
     * 
     * @param genres
     *     The genres
     */
    @JsonProperty("genres")
    public void setGenres(List<HBGenre> genres) {
        this.genres = genres;
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
