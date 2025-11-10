package kr.co.syrup.adreport.web.event.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
@ToString
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "WEB_EVENT_IP_ACCESS")
public class WebEventIpAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idx;

    private String ipAddress;

    private String urlPath;

    private String buildLevel;
}
