package kr.co.syrup.adreport.web.event.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "xtrans_receiver")
public class XtransReceiverEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "employee_number", nullable = false, length = 45)
    private String employeeNumber;

    @Column(name = "employee_name", nullable = false, length = 45)
    private String employeeName;

}