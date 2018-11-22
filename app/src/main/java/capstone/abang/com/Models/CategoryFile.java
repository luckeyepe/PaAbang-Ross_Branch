package capstone.abang.com.Models;

/**
 * Created by Rylai on 1/19/2018.
 */

public class CategoryFile {
    private String CatCode;
    private String CatDesc;
    private String CatImage;
    private String CatStatus;

    public CategoryFile(){

    }

    public CategoryFile(String catCode, String catDesc, String catImage, String catStatus) {
        CatCode = catCode;
        CatDesc = catDesc;
        CatImage = catImage;
        CatStatus = catStatus;
    }

    public String getCatCode() {
        return CatCode;
    }

    public void setCatCode(String catCode) {
        CatCode = catCode;
    }

    public String getCatDesc() {
        return CatDesc;
    }

    public void setCatDesc(String catDesc) {
        CatDesc = catDesc;
    }

    public String getCatImage() {
        return CatImage;
    }

    public void setCatImage(String catImage) {
        CatImage = catImage;
    }

    public String getCatStatus() {
        return CatStatus;
    }

    public void setCatStatus(String catStatus) {
        CatStatus = catStatus;
    }
}
