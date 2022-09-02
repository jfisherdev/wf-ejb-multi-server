package com.jfisherdev.wfejbmultiserver.customer.api;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * @author Josh Fisher
 */
public class CustomerSatisfactionRating implements Serializable {

    private long customerId = -1;
    private int rating = 0;
    private Instant ratingTime;

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Instant getRatingTime() {
        return ratingTime;
    }

    public void setRatingTime(Instant ratingTime) {
        this.ratingTime = ratingTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerSatisfactionRating that = (CustomerSatisfactionRating) o;
        return customerId == that.customerId && rating == that.rating && ratingTime.equals(that.ratingTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, rating, ratingTime);
    }

    @Override
    public String toString() {
        return "CustomerSatisfactionRating{" +
                "customerId=" + customerId +
                ", rating=" + rating +
                ", ratingTime=" + ratingTime +
                '}';
    }
}
