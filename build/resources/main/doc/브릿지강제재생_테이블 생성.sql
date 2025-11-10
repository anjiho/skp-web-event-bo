alter table ar_event_object
    add bridge_force_exposure_time_type varchar(10) default 'N' null comment '브릿지 강제 노출 여부' after `3d_object_position_z`;

alter table ar_event_object
    add bridge_force_exposure_time_second int default '0' comment '브릿지 강제 노출 시간' after bridge_force_exposure_time_type;


alter table ar_event_logical
    add bridge_force_exposure_time_type varchar(10) default 'N' null comment '브릿지 강제 노출 여부' after `3d_object_position_z`;

alter table ar_event_logical
    add bridge_force_exposure_time_second int default '0' comment '브릿지 강제 노출 시간' after bridge_force_exposure_time_type;