package webit.android.shanti.main.info.classes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 1 on 4/12/2015.
 */
public class PlaceDetails {
    @SerializedName("formatted_address")
    private String formatted_address;
    @SerializedName("geometry")
    private Geometry geometry;
    @SerializedName("id")
    private String id;
    @SerializedName("international_phone_number")
    private String international_phone_number;
    @SerializedName("name")
    private String name;

        @SerializedName("photos")
    private List<Photo> photos;
    @SerializedName("place_id")
    private String place_id;
//    @SerializedName("rating")
//    private Double rating;
    @SerializedName("reference")
    private String reference;
    @SerializedName("reviews")
    List<Review> reviews;
    @SerializedName("scope")
    private String scope;
    @SerializedName("types")
    List<String> types;
    @SerializedName("url")
    private String url;
    @SerializedName("user_ratings_total")
    private int user_ratings_total;
    @SerializedName("utc_offset")
    private int utc_offset;
    @SerializedName("vicinity")
    private String vicinity;
    @SerializedName("website")
    private String website;

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInternational_phone_number() {
        return international_phone_number;
    }

    public void setInternational_phone_number(String international_phone_number) {
        this.international_phone_number = international_phone_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

//    public Double getRating() {
//        return rating;
//    }

//    public void setRating(Double rating) {
//        this.rating = rating;
//    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getUser_ratings_total() {
        return user_ratings_total;
    }

    public void setUser_ratings_total(int user_ratings_total) {
        this.user_ratings_total = user_ratings_total;
    }

    public int getUtc_offset() {
        return utc_offset;
    }

    public void setUtc_offset(int utc_offset) {
        this.utc_offset = utc_offset;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}

