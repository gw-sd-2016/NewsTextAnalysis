package models;

/**
 * Created by ellenlouie on 3/5/16.
 */
public class Nonprofit {

    private String name;
    private String donationLink;

    /*--- GET & SET NAME ---*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*--- GET & SET DONATION LINK ---*/

    public String getDonationLink() {
        return donationLink;
    }

    public void setDonationLink(String donationLink) {
        this.donationLink = donationLink;
    }

    @Override
    public String toString() {
        return "Nonprofit[Name: " + name + ", Donation Link: " + donationLink + "]";
    }
}
