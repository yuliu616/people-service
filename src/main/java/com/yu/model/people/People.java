package com.yu.model.people;

import com.yu.model.IHasId;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class People implements IHasId {

    private String id;

    private long version;

    private Instant creationDate;

    private Instant lastUpdated;

    /**
     * a person that is inactive means we ignore him/her,
     * it may because we dont care, or he/she is already dead.
     */
    private boolean isActive;

    /**
     * name that you call a friend
     */
    private String nickname;

    @NotNull
    private Gender gender;

    /**
     * date that this person born, including year,
     * local to where he/she was born.
     */
    private LocalDate dateOfBirth;

    /**
     * for eastern people, 'family name' that common to all children within a family.
     */
    @NotBlank
    private String firstName;

    /**
     * for western people, 'family name' that common to all children within a family.
     */
    @NotBlank
    private String lastName;

    @DecimalMin("0.01")
    private BigDecimal heightInCm;

    @DecimalMin("0.01")
    private BigDecimal weightInKg;

    public People() {}

    public People(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public BigDecimal getHeightInCm() {
        return heightInCm;
    }

    public void setHeightInCm(BigDecimal heightInCm) {
        this.heightInCm = heightInCm;
    }

    public BigDecimal getWeightInKg() {
        return weightInKg;
    }

    public void setWeightInKg(BigDecimal weightInKg) {
        this.weightInKg = weightInKg;
    }
}
