package com.cmaunder.bustracker.vehicleActivity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"recordedAtTime", "vehicleRef"})
})
public class VehicleActivity {

    public VehicleActivity() {
    }

    public VehicleActivity(LocalDateTime recordedAtTime, String itemIdentifier, LocalDateTime validUntilTime, Direction directionRef, String publishedLineName, String operatorRef, String originName, String destinationName, String latitude, String longitude, String blockRef, String vehicleRef, double bearing) {
        this.recordedAtTime = recordedAtTime;
        this.itemIdentifier = itemIdentifier;
        this.validUntilTime = validUntilTime;
        this.directionRef = directionRef;
        this.publishedLineName = publishedLineName;
        this.operatorRef = operatorRef;
        this.originName = originName;
        this.destinationName = destinationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.blockRef = blockRef;
        this.vehicleRef = vehicleRef;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private LocalDateTime recordedAtTime;
    @Id
    private String itemIdentifier;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime validUntilTime;
    private Direction directionRef;
    private String publishedLineName;
    private String operatorRef;
    private String originRef;
    private String originName;
    private String destinationRef;
    private String destinationName;
    private String longitude;
    private String latitude;
    private String blockRef;
    private String vehicleRef;
    private double bearing;

    public OffsetDateTime getRecordedAtTime() {
        return recordedAtTime.atOffset(ZoneOffset.UTC);
    }

    public void setRecordedAtTime(LocalDateTime recordedAtTime) {
        this.recordedAtTime = recordedAtTime;
    }

    public String getItemIdentifier() {
        return itemIdentifier;
    }

    public void setItemIdentifier(String itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }

    public LocalDateTime getValidUntilTime() {
        return validUntilTime;
    }

    public void setValidUntilTime(LocalDateTime validUntilTime) {
        this.validUntilTime = validUntilTime;
    }

    public Direction getDirectionRef() {
        return directionRef;
    }

    public void setDirectionRef(Direction directionRef) {
        this.directionRef = directionRef;
    }

    public String getPublishedLineName() {
        return publishedLineName;
    }

    public void setPublishedLineName(String publishedLineName) {
        this.publishedLineName = publishedLineName;
    }

    public String getOperatorRef() {
        return operatorRef;
    }

    public void setOperatorRef(String operatorRef) {
        this.operatorRef = operatorRef;
    }

    public String getOriginRef() {
        return originRef;
    }

    public void setOriginRef(String originRef) {
        this.originRef = originRef;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getDestinationRef() {
        return destinationRef;
    }

    public void setDestinationRef(String destinationRef) {
        this.destinationRef = destinationRef;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getBlockRef() {
        return blockRef;
    }

    public void setBlockRef(String blockRef) {
        this.blockRef = blockRef;
    }

    public String getVehicleRef() {
        return vehicleRef;
    }

    public void setVehicleRef(String vehicleRef) {
        this.vehicleRef = vehicleRef;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    @Override
    public String toString() {
        return "VehicleActivity{" +
                "recordedAtTime=" + recordedAtTime +
                ", itemIdentifier='" + itemIdentifier + '\'' +
                ", validUntilTime=" + validUntilTime +
                ", directionRef=" + directionRef +
                ", publishedLineName='" + publishedLineName + '\'' +
                ", operatorRef='" + operatorRef + '\'' +
                ", originRef='" + originRef + '\'' +
                ", originName='" + originName + '\'' +
                ", destinationRef='" + destinationRef + '\'' +
                ", destinationName='" + destinationName + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", blockRef='" + blockRef + '\'' +
                ", vehicleRef='" + vehicleRef + '\'' +
                ", bearing=" + bearing +
                '}';
    }
}
