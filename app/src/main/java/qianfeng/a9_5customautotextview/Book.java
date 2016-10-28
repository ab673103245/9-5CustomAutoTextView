package qianfeng.a9_5customautotextview;

/**
 * Created by Administrator on 2016/10/28 0028.
 */
public class Book {
    private int bookImg;
    private String bookName;
    private String bookAuthor;

    public Book() {
    }

    public int getBookImg() {
        return bookImg;
    }

    public void setBookImg(int bookImg) {
        this.bookImg = bookImg;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public Book(int bookImg, String bookName, String bookAuthor) {
        this.bookImg = bookImg;
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
    }
}
