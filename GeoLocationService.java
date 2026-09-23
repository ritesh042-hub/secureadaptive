package com.example.secureadaptive.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class GeoLocationService {

    private final RestTemplate restTemplate = new RestTemplate();

    public GeoLocation getGeoLocation(String ip) {
        try {
            // For local development, resolve the public IP if local IP is detected
            if (ip == null || ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
                try {
                    String publicIp = restTemplate.getForObject("http://checkip.amazonaws.com", String.class);
                    if (publicIp != null) {
                        ip = publicIp.trim();
                    }
                } catch (Exception ignored) {
                    // Ignore failure to resolve public IP and proceed with local IP (will just return Unknown)
                }
            }

            // Call ip-api.com
            String url = "http://ip-api.com/json/" + ip;
            Map response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && "success".equals(response.get("status"))) {
                GeoLocation geo = new GeoLocation();
                geo.setIp(ip);
                geo.setCity((String) response.get("city"));
                geo.setRegion((String) response.get("regionName"));
                geo.setCountry((String) response.get("country"));
                geo.setCountryCode((String) response.get("countryCode"));
                if (response.get("lat") != null) {
                    geo.setLatitude(((Number) response.get("lat")).doubleValue());
                }
                if (response.get("lon") != null) {
                    geo.setLongitude(((Number) response.get("lon")).doubleValue());
                }
                return geo;
            }
        } catch (Exception e) {
            System.err.println("Geolocation failed for IP: " + ip + " - " + e.getMessage());
        }
        
        // Return a default "Unknown" location safely
        GeoLocation defaultGeo = new GeoLocation();
        defaultGeo.setIp(ip);
        defaultGeo.setCity("Unknown");
        defaultGeo.setRegion("Unknown");
        defaultGeo.setCountry("Unknown");
        return defaultGeo;
    }

    public static class GeoLocation {
        private String ip;
        private String city;
        private String region;
        private String country;
        private String countryCode;
        private Double latitude;
        private Double longitude;

        // Getters and Setters
        public String getIp() { return ip; }
        public void setIp(String ip) { this.ip = ip; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getCountryCode() { return countryCode; }
        public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
    }
}
