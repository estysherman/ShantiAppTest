package webit.android.shanti.main.info.classes;

/**
 * Created by 1 on 4/12/2015.
 */
public class Review {
    private String author_name;
    private String author_url;
    private String language;
    private int rating;
    private String text;
    private String time;

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_url() {
        return author_url;
    }

    public void setAuthor_url(String author_url) {
        this.author_url = author_url;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Review(String author_name, String author_url, String language, int rating, String text, String time) {
        this.author_name = author_name;
        this.author_url = author_url;
        this.language = language;
        this.rating = rating;
        this.text = text;
        this.time = time;
    }

    public Review() {
    }
}
