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
    "user_id",
    "item_id",
    "item_type",
    "created_at",
    "updated_at",
    "fav_rank"
})
public class HBFavorite {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("user_id")
    private Integer userId;
    @JsonProperty("item_id")
    private Integer itemId;
    @JsonProperty("item_type")
    private String itemType;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("fav_rank")
    private Integer favRank;
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
     *     The userId
     */
    @JsonProperty("user_id")
    public Integer getUserId() {
        return userId;
    }

    /**
     * 
     * @param userId
     *     The user_id
     */
    @JsonProperty("user_id")
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 
     * @return
     *     The itemId
     */
    @JsonProperty("item_id")
    public Integer getItemId() {
        return itemId;
    }

    /**
     * 
     * @param itemId
     *     The item_id
     */
    @JsonProperty("item_id")
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * 
     * @return
     *     The itemType
     */
    @JsonProperty("item_type")
    public String getItemType() {
        return itemType;
    }

    /**
     * 
     * @param itemType
     *     The item_type
     */
    @JsonProperty("item_type")
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    /**
     * 
     * @return
     *     The createdAt
     */
    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     * @param createdAt
     *     The created_at
     */
    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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
     *     The favRank
     */
    @JsonProperty("fav_rank")
    public Integer getFavRank() {
        return favRank;
    }

    /**
     * 
     * @param favRank
     *     The fav_rank
     */
    @JsonProperty("fav_rank")
    public void setFavRank(Integer favRank) {
        this.favRank = favRank;
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
