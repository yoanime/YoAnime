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
    "name",
    "waifu",
    "waifu_or_husbando",
    "waifu_slug",
    "waifu_char_id",
    "location",
    "website",
    "avatar",
    "cover_image",
    "settings_about",
    "bio",
    "karma",
    "life_spent_on_anime",
    "show_adult_content",
    "title_language_preference",
    "last_library_update",
    "following",
    "favorites"
})
public class HBUser {

    @JsonProperty("name")
    private String name;
    @JsonProperty("waifu")
    private String waifu;
    @JsonProperty("waifu_or_husbando")
    private String waifuOrHusbando;
    @JsonProperty("waifu_slug")
    private String waifuSlug;
    @JsonProperty("waifu_char_id")
    private String waifuCharId;
    @JsonProperty("location")
    private String location;
    @JsonProperty("website")
    private String website;
    @JsonProperty("avatar")
    private String avatar;
    @JsonProperty("cover_image")
    private String coverImage;
    @JsonProperty("settings_about")
    private String about;
    @JsonProperty("bio")
    private String bio;
    @JsonProperty("karma")
    private Integer karma;
    @JsonProperty("life_spent_on_anime")
    private Integer lifeSpentOnAnime;
    @JsonProperty("show_adult_content")
    private Boolean showAdultContent;
    @JsonProperty("title_language_preference")
    private String titleLanguagePreference;
    @JsonProperty("last_library_update")
    private String lastLibraryUpdate;
    @JsonProperty("following")
    private Boolean following;
    @JsonProperty("favorites")
    private List<HBFavorite> favorites = new ArrayList<HBFavorite>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The waifu
     */
    @JsonProperty("waifu")
    public String getWaifu() {
        return waifu;
    }

    /**
     * 
     * @param waifu
     *     The waifu
     */
    @JsonProperty("waifu")
    public void setWaifu(String waifu) {
        this.waifu = waifu;
    }

    /**
     * 
     * @return
     *     The waifuOrHusbando
     */
    @JsonProperty("waifu_or_husbando")
    public String getWaifuOrHusbando() {
        return waifuOrHusbando;
    }

    /**
     * 
     * @param waifuOrHusbando
     *     The waifu_or_husbando
     */
    @JsonProperty("waifu_or_husbando")
    public void setWaifuOrHusbando(String waifuOrHusbando) {
        this.waifuOrHusbando = waifuOrHusbando;
    }

    /**
     * 
     * @return
     *     The waifuSlug
     */
    @JsonProperty("waifu_slug")
    public String getWaifuSlug() {
        return waifuSlug;
    }

    /**
     * 
     * @param waifuSlug
     *     The waifu_slug
     */
    @JsonProperty("waifu_slug")
    public void setWaifuSlug(String waifuSlug) {
        this.waifuSlug = waifuSlug;
    }

    /**
     * 
     * @return
     *     The waifuCharId
     */
    @JsonProperty("waifu_char_id")
    public String getWaifuCharId() {
        return waifuCharId;
    }

    /**
     * 
     * @param waifuCharId
     *     The waifu_char_id
     */
    @JsonProperty("waifu_char_id")
    public void setWaifuCharId(String waifuCharId) {
        this.waifuCharId = waifuCharId;
    }

    /**
     * 
     * @return
     *     The location
     */
    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    /**
     * 
     * @param location
     *     The location
     */
    @JsonProperty("location")
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * 
     * @return
     *     The website
     */
    @JsonProperty("website")
    public String getWebsite() {
        return website;
    }

    /**
     * 
     * @param website
     *     The website
     */
    @JsonProperty("website")
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * 
     * @return
     *     The avatar
     */
    @JsonProperty("avatar")
    public String getAvatar() {
        return avatar;
    }

    /**
     * 
     * @param avatar
     *     The avatar
     */
    @JsonProperty("avatar")
    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
     *     The settings_about
     */
    @JsonProperty("settings_about")
    public String getAbout() {
        return about;
    }

    /**
     * 
     * @param about
     *     The settings_about
     */
    @JsonProperty("settings_about")
    public void setAbout(String about) {
        this.about = about;
    }

    /**
     * 
     * @return
     *     The bio
     */
    @JsonProperty("bio")
    public String getBio() {
        return bio;
    }

    /**
     * 
     * @param bio
     *     The bio
     */
    @JsonProperty("bio")
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * 
     * @return
     *     The karma
     */
    @JsonProperty("karma")
    public Integer getKarma() {
        return karma;
    }

    /**
     * 
     * @param karma
     *     The karma
     */
    @JsonProperty("karma")
    public void setKarma(Integer karma) {
        this.karma = karma;
    }

    /**
     * 
     * @return
     *     The lifeSpentOnAnime
     */
    @JsonProperty("life_spent_on_anime")
    public Integer getLifeSpentOnAnime() {
        return lifeSpentOnAnime;
    }

    /**
     * 
     * @param lifeSpentOnAnime
     *     The life_spent_on_anime
     */
    @JsonProperty("life_spent_on_anime")
    public void setLifeSpentOnAnime(Integer lifeSpentOnAnime) {
        this.lifeSpentOnAnime = lifeSpentOnAnime;
    }

    /**
     * 
     * @return
     *     The showAdultContent
     */
    @JsonProperty("show_adult_content")
    public Boolean getShowAdultContent() {
        return showAdultContent;
    }

    /**
     * 
     * @param showAdultContent
     *     The show_adult_content
     */
    @JsonProperty("show_adult_content")
    public void setShowAdultContent(Boolean showAdultContent) {
        this.showAdultContent = showAdultContent;
    }

    /**
     * 
     * @return
     *     The titleLanguagePreference
     */
    @JsonProperty("title_language_preference")
    public String getTitleLanguagePreference() {
        return titleLanguagePreference;
    }

    /**
     * 
     * @param titleLanguagePreference
     *     The title_language_preference
     */
    @JsonProperty("title_language_preference")
    public void setTitleLanguagePreference(String titleLanguagePreference) {
        this.titleLanguagePreference = titleLanguagePreference;
    }

    /**
     * 
     * @return
     *     The lastLibraryUpdate
     */
    @JsonProperty("last_library_update")
    public String getLastLibraryUpdate() {
        return lastLibraryUpdate;
    }

    /**
     * 
     * @param lastLibraryUpdate
     *     The last_library_update
     */
    @JsonProperty("last_library_update")
    public void setLastLibraryUpdate(String lastLibraryUpdate) {
        this.lastLibraryUpdate = lastLibraryUpdate;
    }

    /**
     * 
     * @return
     *     The following
     */
    @JsonProperty("following")
    public Boolean getFollowing() {
        return following;
    }

    /**
     * 
     * @param following
     *     The following
     */
    @JsonProperty("following")
    public void setFollowing(Boolean following) {
        this.following = following;
    }

    /**
     * 
     * @return
     *     The favorites
     */
    @JsonProperty("favorites")
    public List<HBFavorite> getFavorites() {
        return favorites;
    }

    /**
     * 
     * @param favorites
     *     The favorites
     */
    @JsonProperty("favorites")
    public void setFavorites(List<HBFavorite> favorites) {
        this.favorites = favorites;
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
