alter table ar_event_logical
    add `3d_object_position_setting_type` varchar(10) null comment '3D 오브젝트 위치 설정 여부 값';

alter table ar_event_logical
    add `3d_object_position_x` varchar(5) null comment '3D 오브젝트 위치(x)';

alter table ar_event_logical
    add `3d_object_position_y` varchar(5) null comment '3D 오브젝트 위치(y)';

alter table ar_event_logical
    add `3d_object_position_z` varchar(5) null comment '3D 오브젝트 위치(z)';

alter table ar_event_logical
    add nft_wallet_img_url varchar(100) null;

alter table ar_event
    add information_provision_agreement_text_setting varchar(1) default 'N' null comment '정보 제공동의 문구설정(N:설정안함, Y:설정)';

alter table ar_event
    add information_provision_recipient varchar(200) null comment '정보 제공동의 문구 - 제공받는 자';

alter table ar_event
    add information_provision_consignor varchar(200) null comment '정보 제공동의 문구 - 위탁업체';

alter table ar_event
    add information_provision_purpose_use varchar(200) null comment '정보 제공동의 문구 - 이용목적';

alter table ar_event_object
    add `3d_object_position_setting_type` varchar(10) null comment '3D 오브젝트 위치 설정 여부 값';

alter table ar_event_object
    add `3d_object_position_x` varchar(5) null comment '3D 오브젝트 위치(x)';

alter table ar_event_object
    add `3d_object_position_y` varchar(5) null comment '3D 오브젝트 위치(y)';

alter table ar_event_object
    add `3d_object_position_z` varchar(5) null comment '3D 오브젝트 위치(z)';

alter table ar_event_winning
    add nft_img_url varchar(255) null comment 'NFT 상품 이미지 URL';

alter table ar_event_winning
    add nft_inactive_img_url varchar(255) null comment 'NFT 상품 비활성 이미지 URL';

alter table ar_event_winning
    add nft_ownership_transfer_date_assign_yn varchar(1) default 'N' null comment 'NFT 소유권이전일 등록여부( N : 설정안함 ,  Y : 날짜지정)';

alter table ar_event_winning
    add nft_ownership_transfer_date datetime null comment 'NFT 소유권이전일(YYYY-MM-DD HH:MM:SS)';

alter table ar_event_winning
    add nft_benefit_reg_yn varchar(1) null comment 'NFT 혜약 등록(N : 선택안함, Y : 혜택등록)';

alter table ar_event_winning
    add subscription_yn varchar(1) default 'N' null comment '응모여부';

alter table ar_event_winning
    add subscription_winning_number int null comment '응모 당첨수(건수)';

alter table ar_event_winning
    add subscription_raffle_date datetime null comment '응모 추첨일 (년월일시)';

alter table ar_event_winning
    add is_subscription_raffle tinyint(1) default 0 null comment '응모 추첨 여부';

alter table ar_event_winning
    add subscription_winning_presentation_date datetime null comment '응모 당첨 결과 발표일(년월일시)';

alter table ar_event_winning
    add is_subscription_winning_presentation tinyint(1) default 0 null comment '응모 당첨 결과 발표 여부';

alter table ar_event_winning
    add subscription_raffle_schedule_date datetime null comment '응모 추첨 스케쥴링 시간(스케쥴링)';

alter table ar_event_winning
    add nft_excel_upload_file_name varchar(200) null comment 'NFT 토큰 엑셀 업로드 파일명';

create table ar_event_nft_banner
(
    ar_nft_banner_id   int auto_increment comment '인덱스'
        primary key,
    ar_event_id        int                                not null comment 'AR_EVENT.id',
    event_html_id      int                                null,
    banner_img_url     varchar(200)                       null comment '배너 이미지 URL',
    banner_target_url  varchar(200)                       null comment '배너 타겟 URL',
    banner_sort        int unsigned                       null,
    created_by         varchar(50)                        null comment '생성자',
    created_date       datetime default CURRENT_TIMESTAMP not null comment '생성일',
    last_modified_by   varchar(50)                        null comment '수정자',
    last_modified_date datetime                           null comment '수정일'
)
    comment 'NFT 배너 정보';

create table ar_event_nft_benefit
(
    ar_event_nft_benefit_id int auto_increment comment '인덱스'
        primary key,
    ar_event_winning_id     int                                not null comment '당첨정보 아이디',
    nft_benefit_name        varchar(100)                       null comment '혜택명',
    nft_benefit_desc        varchar(200)                       null comment '혜택 부가 설명',
    nft_benefit_sort        int                                null comment '순서',
    created_by              varchar(50)                        null comment '생성자',
    created_date            datetime default CURRENT_TIMESTAMP null comment '생성일',
    last_modified_by        varchar(50)                        null comment '수정자',
    last_modified_date      datetime                           null comment '수정일'
)
    comment 'NFT 혜택 정보 리스트';

create table ar_event_nft_coupon_info
(
    id                     bigint auto_increment comment '인덱스'
        primary key,
    ar_event_id            int                                  null comment '이벤트 아이디',
    ar_event_winning_id    int                                  null comment '당첨 정보 아이디',
    nft_coupon_id          varchar(255)                         not null comment '토큰 아이디',
    is_payed               tinyint(1) default 0                 not null comment '지급여부',
    upload_excel_file_name varchar(200)                         not null comment '파일업로드 엑셀 파일 명',
    created_date           datetime   default CURRENT_TIMESTAMP not null comment '생성일'
)
    comment 'NFT 쿠폰 정보';

create table ar_event_nft_coupon_repository
(
    ar_nft_coupon_repository_id bigint auto_increment comment '인덱스'
        primary key,
    nft_coupon_info_id          bigint                               not null comment 'AR_EVENT_NFT_COUPON_INFO.id',
    give_away_id                int                                  not null comment '이벤트 경품 배송 정보 인덱스',
    is_use                      tinyint(1) default 0                 not null comment '쿠폰 사용 여부',
    use_date                    datetime                             null comment '쿠폰 사용 시간',
    created_date                datetime   default CURRENT_TIMESTAMP not null comment '생성일'
)
    comment 'NFT 쿠폰 지급 보관함';

create table ar_event_nft_repository
(
    ar_nft_repository_id       bigint auto_increment comment '인덱스'
        primary key,
    ar_event_nft_wallet_id     int                                null comment 'AR_NFT_WALLET.index',
    give_away_id               bigint                             not null comment '이벤트 경품 배송 정보 인덱스',
    ar_event_nft_token_info_id bigint                             null comment 'AR_EVENT_NFT_TOKEN_INFO.id',
    nft_trance_date            datetime                           null comment 'NFT 이전 시각',
    holder_trance_status       int      default 0                 not null comment '소유권 이전 여부(0 : 이전전, 1 : 이전중, 2 : 이전완료)',
    request_id                 varchar(100)                       null comment '소유권 이전완료되면 결과 값',
    nft_webhook_result         text                               null comment 'nft webhook 결과값 ',
    created_date               datetime default CURRENT_TIMESTAMP not null comment '생성일'
)
    comment 'NFT 보관함';

create table ar_event_nft_token_info
(
    id                     bigint auto_increment comment '인덱스'
        primary key,
    ar_event_id            int                                  null comment '이벤트 아이디',
    ar_event_winning_id    int                                  null comment '당첨 정보 아이디',
    nft_token_id           varchar(255)                         not null comment '토큰 아이디',
    is_payed               tinyint(1) default 0                 null comment '지급여부',
    upload_excel_file_name varchar(200)                         null comment '파일업로드 엑셀 파일 명',
    created_date           datetime   default CURRENT_TIMESTAMP not null comment '생성일'
)
    comment 'NFT 토큰 정보';

create table ar_event_nft_wallet
(
    ar_event_nft_wallet_id bigint auto_increment comment '인덱스'
        primary key,
    ar_event_id            int                                   not null comment 'AR 이벤트 아이디',
    user_phone_number      varchar(200)                          not null comment '사용자 핸드폰번호',
    nft_wallet_address     varchar(200)                          null comment 'NFT 지갑 주소',
    nft_wallet_type        varchar(20) default 'KAS'             not null comment 'NFT 지갑 종류(KAS : 카이카스, ETH : 이더리움)',
    created_by             varchar(50)                           null comment '생성자',
    created_date           datetime    default CURRENT_TIMESTAMP not null comment '생성일',
    last_modified_by       varchar(50)                           null comment '수정자',
    last_modified_date     datetime                              null comment '수정일'
)
    comment 'NFT 지갑 정보';

create table event_log_sms_send
(
    id                    bigint auto_increment comment '인덱스'
        primary key,
    give_away_id          int                                  null,
    ar_event_winning_id   int                                  null comment 'SMS발송코드(giveAway + 핸드폰번호 조합)',
    receiver_phone_number varchar(45)                          null comment '수신자 핸드폰번호',
    sms_contents          text                                 null comment 'sms내용',
    send_date             datetime                             null comment '문자 발송 시간',
    is_success            tinyint(1) default 1                 null comment '성공여부',
    created_date          datetime   default CURRENT_TIMESTAMP null comment '생성일'
)
    comment 'SMS 문자 발송 로그';

create table event_log_winning_subscription
(
    id                  bigint auto_increment comment '인덱스'
        primary key,
    ar_event_winning_id int                                  null,
    give_away_id        int                                  not null comment '이벤트 경품 배송 정보 인덱스',
    is_sms_send         tinyint(1) default 0                 null comment 'sms 발송 여부',
    created_date        datetime   default CURRENT_TIMESTAMP not null comment '생성일'
)
    comment '이벤트 NFT 응모 당첨 결과 로그';


drop table history_ar_event;

drop table history_ar_event_attend_time;

drop table history_ar_event_button;

drop table history_ar_event_html;

drop table history_ar_event_logical;

drop table history_ar_event_nft_banner;

drop table history_ar_event_nft_benefit;

drop table history_ar_event_object;

drop table history_ar_event_scanning_image;

drop table history_ar_event_winning;

drop table history_ar_event_winning_button;

drop table history_web_event_base;

create table history_ar_event
(
    his_ar_event_id                              int auto_increment comment '히스토리 AR 이벤트 아이디'
        primary key,
    ori_ar_event_id                              int                                not null comment '원본 테이블 ID',
    his_web_event_base_id                        int                                null,
    event_id                                     varchar(7)                         null comment '이벤트 기본 테이블 아이디',
    event_logical_type                           varchar(10)                        null comment 'AR 구동 정보(기본형 ~ 이미지스캐닝형)',
    location_setting_yn                          tinyint(1)                         null comment '페이지 접속 팝업(위치설정조건)',
    ar_attend_condition_all_yn                   tinyint(1)                         null comment 'AR 참여조건(전체)',
    ar_attend_condition_special_location_yn      tinyint(1)                         null comment 'AR 참여조건(특정위치)',
    ar_attend_condition_hourly_yn                tinyint(1)                         null comment 'AR 참여조건(시간별)',
    ar_attend_condition_code_yn                  tinyint(1)                         null comment 'AR 참여조건(참여번호)',
    ar_attend_term_type                          varchar(10)                        null comment '기간참여조건 타입(제한없음, 기간제한)',
    ar_attend_term_limit_type                    varchar(10)                        null comment '기간참여조건 종류(1일, 이벤트기간내)',
    ar_attend_term_limit_count                   int                                null comment '기간참여조건 회수',
    pid                                          varchar(50)                        null comment 'pid',
    location_message_attend                      varchar(100)                       null comment '위치메세지 등록(위치 참여시)',
    location_message_not_attend                  varchar(100)                       null comment '위치메세지 등록(위치 미 참여시)',
    attend_hour_mis_message                      varchar(100)                       null comment '시간참여 불가시 메세지',
    attend_code_mis_match_message                varchar(100)                       null comment '참여번호 미 매칭시',
    ar_bg_image                                  varchar(400)                       null comment 'AR BG 이미지',
    ar_skin_image                                varchar(400)                       null comment 'AR 스킨 이미지',
    duplicate_winning_type                       varchar(10)                        null comment '당첨정보(공통)설정 > 중복당첨수 제한 타입',
    duplicate_winning_limit_type                 int                                null comment '중복당첨 당첨제한 (전체 : 0, 1일 : 1)',
    duplicate_winning_count                      int                                null comment '중복 당첨 당첨제한 회수',
    winning_password_yn                          varchar(3)                         null comment '경품 비밀번호 타입',
    attend_code_reg_type                         varchar(10)                        null comment '참여번호 등록 종류',
    attend_code_count                            int(10)                            null comment '코드 생성수',
    attend_code_digit                            int(2)                             null comment '코드 자릿수',
    information_provision_agreement_text_setting varchar(200)                       null,
    information_provision_recipient              varchar(200)                       null,
    information_provision_consignor              varchar(200)                       null,
    information_provision_purpose_use            varchar(200)                       null,
    created_by                                   varchar(50)                        null comment '생성자',
    created_date                                 datetime default CURRENT_TIMESTAMP null comment '생성일',
    last_modified_by                             varchar(50)                        null comment '수정자',
    last_modified_date                           datetime                           null comment '수정일'
)
    comment '이벤트 설정(공통) 테이블';

create table history_ar_event_attend_time
(
    his_ar_event_attend_time_id int auto_increment comment '아이디'
        primary key,
    ori_ar_event_attend_time_id int         not null comment '원본 ID',
    his_web_event_base_id       int         null,
    ar_event_id                 int         null comment 'AR 이벤트 아이디',
    attend_start_hour           int         null comment '참여시간 설정(시작)',
    attend_end_hour             int         null comment '참여시간 설정(종료)',
    created_by                  varchar(50) null comment '생성자',
    created_date                datetime    null comment '생성일',
    last_modified_by            varchar(50) null comment '수정자',
    last_modified_date          datetime    null comment '수정일'
)
    comment '이벤트 설정(공통) 참여 시간 테이블' charset = utf8;

create table history_ar_event_button
(
    his_ar_event_button_id           int auto_increment comment '아이디'
        primary key,
    ori_ar_event_button_id           int                                not null,
    his_web_event_base_id            int                                null,
    ar_event_id                      int                                not null comment '이벤트 아이디',
    ar_button_bg_color_assign_type   varchar(10)                        null comment '버튼 배경색 지정 여부 값(AR_EVENT_CATEGORY)',
    ar_button_bg_color_input_type    varchar(10)                        null comment '버튼 배경색 지정일떄 RGB, HEX 여부',
    ar_button_bg_color_red           int(3)                             null comment '버튼 배경색 rgb 값',
    ar_button_bg_color_green         int(3)                             null comment '버튼 배경색 rgb 값',
    ar_button_bg_color_blue          int(3)                             null comment '버튼 배경색 rgb 값',
    ar_button_bg_color_hex           varchar(10)                        null comment '버튼 배경색 hex 값',
    ar_button_color_assign_type      varchar(10)                        null comment '버튼색 지정 여부 값',
    ar_button_color_input_type       varchar(10)                        null comment '버튼색 지정일떄 RGB, HEX 여부',
    ar_button_color_red              int(3)                             null comment '버튼색 rgb 값',
    ar_button_color_green            int(3)                             null comment '버튼색 rgb 값',
    ar_button_color_blue             int(3)                             null comment '버튼색 rgb 값',
    ar_button_color_hex              varchar(10)                        null comment '버튼색 hex',
    ar_button_text_color_assign_type varchar(10)                        null comment '버튼 text 색 지정 여부 값',
    ar_button_text_color_input_type  varchar(10)                        null comment '버튼 text 색 지정일떄 RGB, HEX 여부',
    ar_button_text_color_red         int(3)                             null comment '버튼 text 색 rgb값',
    ar_button_text_color_green       int(3)                             null comment '버튼 text 색 rgb값',
    ar_button_text_color_blue        int(3)                             null comment '버튼 text 색 rgb값',
    ar_button_text_color_hex         varchar(10)                        null comment '버튼 text 색 hext값',
    ar_button_text                   varchar(50)                        null comment '버튼 text 문구 지정',
    created_by                       varchar(50)                        null comment '생성자',
    created_date                     datetime default CURRENT_TIMESTAMP null comment '생성일',
    last_modified_by                 varchar(50)                        null comment '수정자',
    last_modified_date               datetime                           null comment '수정일'
)
    comment '이벤트 설정 버튼 정보' charset = utf8;

create table history_ar_event_html
(
    his_event_html_id                  int auto_increment comment '아이디'
        primary key,
    ori_event_html_id                  int                                not null,
    his_web_event_base_id              int                                null,
    event_id                           varchar(7)                         null,
    ar_event_id                        int                                null comment '이벤트 아이디',
    html_type                          varchar(10)                        null comment 'html 정보 타입(IMAGE:이미지, BUTTON:버튼, SHARE:공유하기)',
    html_type_sort                     int                                null comment '순서',
    html_image_url                     varchar(100)                       null comment '이미지 url',
    html_button_type                   varchar(10)                        null comment '버튼 유형',
    html_button_bg_color_assign_type   varchar(10)                        null comment '버튼 배경색 지정여부',
    html_button_bg_color_input_type    varchar(10)                        null comment '버튼 배경색 지정일떄 RGB, HEX 여부)',
    html_button_bg_color_red           int(3)                             null comment '버튼 배경색 rgb 값',
    html_button_bg_color_green         int(3)                             null comment '버튼 배경색 rgb 값',
    html_button_bg_color_blue          int(3)                             null comment '버튼 배경색 rgb 값',
    html_button_bg_color_hex           varchar(10)                        null comment '버튼 배경색 hex 값',
    html_button_text                   varchar(20)                        null comment '버튼 text',
    html_button_target_url             varchar(100)                       null comment '버튼 target url',
    html_share_button_image_url        varchar(100)                       null comment '공유하기 버튼 이미지 url',
    html_button_color_assign_type      varchar(10)                        null comment '버튼색 지정여부',
    html_button_color_input_type       varchar(10)                        null comment '버튼색 지정일떄 RGB, HEX 여부)',
    html_button_color_red              int(3)                             null comment '버튼색 rgb 값',
    html_button_color_green            int(3)                             null comment '버튼색 rgb 값',
    html_button_color_blue             int(3)                             null comment '버튼색 rgb 값',
    html_button_color_hex              varchar(10)                        null comment '버튼색 hex 값',
    html_button_text_color_assign_type varchar(10)                        null comment '버튼 텍스트색 지정여부',
    html_button_text_color_input_type  varchar(10)                        null comment '버튼 테스트색 지정일떄 RGB, HEX 여부)',
    html_button_text_color_red         int(3)                             null comment '버튼 테스트색 rgb값',
    html_button_text_color_green       int(3)                             null comment '버튼 테스트색 rgb값',
    html_button_text_color_blue        int(3)                             null comment '버튼 테스트색 rgb값',
    html_button_text_color_hex         varchar(10)                        null comment '버튼 테스트색 rgb값',
    kakao_share_thumbnail_url          varchar(200)                       null comment '카카오톡 공유하기 썸네일 url',
    kakao_share_contents               varchar(50)                        null comment '카카오톡 공유하기 내용',
    created_by                         varchar(50)                        null comment '생성자',
    created_date                       datetime default CURRENT_TIMESTAMP null comment '생성일',
    last_modified_by                   varchar(50)                        null comment '수정자',
    last_modified_date                 datetime                           null comment '수정일'
)
    comment '이벤트 이미지, 버튼, 공유하기 정보';

create table history_ar_event_logical
(
    his_ar_event_logical_id           int auto_increment comment '아이디'
        primary key,
    ori_ar_event_logical_id           int          not null,
    his_web_event_base_id             int          null,
    ar_event_id                       int          null comment '이벤트 아이디',
    pan_position_type                 varchar(10)  null comment '판 설정  값(판 위치 셀렉트박스)',
    pan_mission_number                int          null comment '판 미션 수',
    bridge_type                       varchar(10)  null comment '브릿지 타입 값',
    bridge_url                        varchar(200) null comment '브릿지 url',
    bridge_exposure_time_type         varchar(10)  null comment '브릿지 노출 시간 여부 값(설정 라디오버튼)',
    bridge_exposure_time_second       int          null comment '브릿지 노출 시간 값',
    bridge_display_direction_type     varchar(10)  null comment '브릿지 화면 방향  값(화면 방향 라디오 코드 값)',
    bridge_object_size_x              varchar(10)  null comment '브릿지 크기 X',
    bridge_object_size_y              varchar(10)  null comment '브릿지 크기 Y',
    bridge_object_size_z              varchar(10)  null comment '브릿지 크기 Z',
    `3d_object_position_setting_type` varchar(10)  null,
    `3d_object_position_x`            varchar(5)   null,
    `3d_object_position_y`            varchar(5)   null,
    `3d_object_position_z`            varchar(5)   null,
    created_by                        varchar(50)  null comment '생성자',
    created_date                      datetime     null comment '생성일',
    last_modified_by                  varchar(50)  null comment '수정자',
    last_modified_date                datetime     null comment '수정일',
    nft_wallet_img_url                varchar(100) null
)
    comment 'AR 구동정보 공통 테이블' charset = utf8;

create table history_ar_event_nft_banner
(
    his_ar_nft_banner_id  int auto_increment comment '아이디'
        primary key,
    ori_ar_nft_banner_id  int                                not null,
    his_web_event_base_id int                                null,
    ar_event_id           int                                not null comment 'AR_EVENT.id',
    event_html_id         int                                null,
    banner_img_url        varchar(200)                       null comment '배너 이미지 URL',
    banner_target_url     varchar(200)                       null comment '배너 타겟 URL',
    created_by            varchar(50)                        null comment '생성자',
    created_date          datetime default CURRENT_TIMESTAMP not null comment '생성일',
    last_modified_by      varchar(50)                        null comment '수정자',
    last_modified_date    datetime                           null comment '수정일',
    banner_sort           int unsigned                       null
)
    comment 'NFT 배너 히스토리 정보';

create table history_ar_event_nft_benefit
(
    his_ar_event_nft_benefit_id int auto_increment comment '인덱스'
        primary key,
    ori_ar_event_nft_benefit_id int                                not null,
    his_web_event_base_id       int                                null,
    ar_event_winning_id         int                                not null comment '당첨정보 아이디',
    nft_benefit_name            varchar(100)                       null comment '혜택명',
    nft_benefit_desc            varchar(200)                       null comment '혜택 부가 설명',
    created_by                  varchar(50)                        null comment '생성자',
    created_date                datetime default CURRENT_TIMESTAMP null comment '생성일',
    last_modified_by            varchar(50)                        null comment '수정자',
    last_modified_date          datetime                           null comment '수정일',
    nft_benefit_sort            int                                null comment '순서'
);

create table history_ar_event_object
(
    his_ar_event_object_id            int auto_increment comment '아이디'
        primary key,
    ori_ar_event_object_id            int                                   not null,
    his_web_event_base_id             int                                   null,
    ar_event_id                       int                                   null comment '이벤트 아이디',
    object_sort                       int                                   null comment '오브젝트 순서',
    object_setting_type               varchar(10)                           null comment '오브젝트 설정 값',
    object_setting_url                varchar(100)                          null comment '오브젝트 설정 파일 URL',
    object_size_x                     varchar(10)                           null comment '오브젝트 크기(x)',
    object_size_y                     varchar(10)                           null comment '오브젝트 크기(y)',
    object_size_z                     varchar(10)                           null comment '오브젝트 크기(z)',
    video_play_repeat_type            varchar(10) default '1'               null comment '동영상 재생반복 여부 값',
    object_position_assign_type       varchar(10)                           null comment '오브젝트 위치지정 값',
    object_location_x                 decimal(3, 3)                         null comment '오브젝트 위치 지정(x)',
    object_location_y                 decimal(3, 3)                         null comment '오브젝트 위치 지정(y)',
    object_location_z                 decimal(3, 3)                         null comment '오브젝트 위치 지정(z)',
    stay_effect_type                  varchar(10)                           null comment 'STAY EFFECT 설정  값',
    click_event_type                  varchar(10)                           null comment '클릭 이벤트 설정  값',
    object_change_setting_type        varchar(10)                           null comment '오브젝트 change 설정 값',
    object_change_setting_video_url   varchar(100)                          null comment '오브젝트 change 설정 파일 URL',
    object_change_size_x              varchar(10)                           null comment '오브젝트 change 크기(x)',
    object_change_size_y              varchar(10)                           null comment '오브젝트 change 크기(y)',
    object_change_size_z              varchar(10)                           null comment '오브젝트 change 크기(z)',
    catch_sound_type                  varchar(10)                           null comment '캐치 사운드 설정 값',
    catch_sound_file                  varchar(100)                          null comment '캐치 사운드  값(URL, Library)',
    exposure_control_type             varchar(10)                           null comment '노출제어 값',
    location_exposure_control_type    varchar(10)                           null comment '위치 노출제어 값',
    location_exposure_control_pid     varchar(45)                           null comment '위치 노출제어',
    max_exposure_type                 varchar(10)                           null comment '최대 노출 여부 값',
    max_exposure_count                int                                   null comment '최대 노출 수',
    day_exposure_type                 varchar(10)                           null comment '일 노출 여부  값',
    day_exposure_count                int                                   null comment '일 노출 수',
    hour_exposure_type                varchar(10)                           null comment '시간당 노출 여부 값',
    hour_exposure_count               int                                   null comment '시간당 노출 수',
    attend_code_exposure_type         varchar(10)                           null comment '참여번호당 노출수 타입 값',
    attend_code_limit_type            int                                   null comment '참여번호당 노출수 지정시 타입(0:전체기한내, 1일)',
    attend_code_exposure_count        int                                   null comment '참여번호당 노출수',
    exposure_percent_type             varchar(10)                           null comment '노출 확률 여부 값',
    exposure_percent                  varchar(6)                            null comment '노출 확률 %(0.01 ~ 100)',
    bridge_type                       varchar(10)                           null comment '브릿지 타입 값',
    bridge_url                        varchar(100)                          null comment '브릿지 파일 url',
    bridge_exposure_time_type         varchar(2)                            null,
    bridge_exposure_time_second       int                                   null comment '브릿지 노출 시간 값',
    bridge_display_direction_type     varchar(10)                           null comment '브릿지 화면 방향  값(화면 방향 라디오 코드 값)',
    mission_inactive_thumbnail_url    varchar(100)                          null comment '미션클리어형 비활성 썸네일 url',
    mission_active_thumbnail_url      varchar(100)                          null comment '미션클리어형 활성 썸네일 url',
    created_by                        varchar(50)                           null comment '생성자',
    created_date                      datetime    default CURRENT_TIMESTAMP null comment '생성일',
    last_modified_by                  varchar(50)                           null comment '수정자',
    last_modified_date                datetime                              null comment '수정일',
    object_change_setting_url         varchar(255)                          null,
    object_position_x                 varchar(10)                           null,
    object_position_y                 varchar(10)                           null,
    object_position_z                 varchar(10)                           null,
    bridge_object_size_x              varchar(10)                           null comment '브릿지 오브젝트 크기 X',
    bridge_object_size_y              varchar(10)                           null comment '브릿지 오브젝트 크기 Y',
    bridge_object_size_z              varchar(10)                           null comment '브릿지 오브젝트 크기 Z',
    `3d_object_position_setting_type` varchar(10)                           null,
    `3d_object_position_x`            varchar(5)                            null,
    `3d_object_position_y`            varchar(5)                            null,
    `3d_object_position_z`            varchar(5)                            null
)
    comment 'AR 구동 정보 테이블(기본형, 브릿지형, 미션클리어판)' charset = utf8;

create table history_ar_event_scanning_image
(
    his_ar_event_scanning_image_id int auto_increment comment '아이디'
        primary key,
    ori_ar_event_scanning_image_id int                                not null,
    his_web_event_base_id          int                                null,
    ar_event_id                    int                                null comment '이미지스캐닝 정보 아이디',
    scanning_image_sort            int                                null comment '이미지 설정 넘버',
    scanning_image_url             varchar(100)                       null comment '스캐닝 이미지 url',
    scanning_sound_type            varchar(10)                        null comment '스캐닝 사운드 선택 타입 값',
    scanning_sound_file            varchar(100)                       null comment '스캐닝 사운드 데이터',
    active_thumbnail_url           varchar(100)                       null comment '활성화 썸네일',
    inactive_thumbnail_url         varchar(100)                       null comment '비활성화 썸네일',
    created_by                     varchar(50)                        null comment '생성자',
    created_date                   datetime default CURRENT_TIMESTAMP null comment '생성일',
    last_modified_by               varchar(50)                        null comment '수정자',
    last_modified_date             datetime                           null comment '수정일',
    column_14                      int                                null
)
    comment '이미지스캐닝 스캐닝 이미지 정보 리스트' charset = utf8;

create table history_ar_event_winning
(
    his_ar_event_winning_id                int auto_increment comment '아이디'
        primary key,
    ori_ar_event_winning_id                int                     not null,
    his_web_event_base_id                  int                     null,
    ar_event_id                            int                     null comment '이벤트 아이디',
    event_winning_sort                     int                     null comment '당첨자 정보 설정 넘버',
    object_mapping_type                    varchar(10)             null comment '오브젝트 매핑 선택 타입 값',
    object_mapping_number                  int                     null comment '매핑정보 설정 넘버',
    winning_type                           varchar(10)             null comment '당첨 타입  값(기프티콘, 기타, 꽝)',
    gifticon_product_code                  varchar(100)            null comment '기프티콘 상품 코드 값',
    gifticon_campaign_id                   varchar(100)            null comment '기프티콘 캠패인 ID 값',
    winning_time_type                      varchar(10)             null comment '당첨시간설정 여부  값',
    start_winning_time                     int                     null comment '당첨 시작 시간(0 ~ 23)',
    end_winning_time                       int                     null comment '당첨 종료 시간(1 ~ 24)',
    total_winning_number                   int                     null comment '전체 당첨 수량',
    day_winning_number                     int                     null comment '일 당첨 수량',
    hour_winning_number                    int                     null comment '시간당 당첨 수량',
    winning_percent                        varchar(6)              null comment '당첨률',
    winning_image_url                      varchar(100)            null comment '당첨 이미지 url',
    product_name                           varchar(50)             null comment '당첨 상품명',
    attend_code_winning_type               varchar(45) default 'N' null comment '참여번호당 당첨제한 타입 값',
    attend_code_limit_type                 int                     null comment '참여번호당 당첨제한 (전체 : 0, 1일 : 1)',
    attend_code_winning_count              int                     null comment '참여번호당 당첨제한 회수',
    nft_img_url                            varchar(255)            null,
    nft_ownership_transfer_date_assign_yn  varchar(1)              null,
    nft_ownership_transfer_date            datetime                null,
    nft_benefit_reg_yn                     varchar(1)              null,
    subscription_yn                        varchar(1)              null,
    subscription_winning_number            int                     null,
    subscription_raffle_date               datetime                null,
    subscription_winning_presentation_date datetime                null,
    is_subscription_winning_presentation   tinyint(1)              null,
    created_by                             varchar(50)             null comment '생성자',
    created_date                           datetime                null comment '생성일',
    last_modified_by                       varchar(50)             null comment '수정자',
    last_modified_date                     datetime                null comment '수정일',
    subscription_raffle_schedule_date      datetime                null comment '응모 추첨 스케쥴링 시간(스케쥴링)',
    nft_excel_upload_file_name             varchar(200)            null comment 'NFT 토큰 엑셀 업로드 파일명',
    is_subscription_raffle                 tinyint(1)  default 0   null comment '응모 추첨 여부'
)
    comment '이벤트 당첨자 정보 설정';

create table history_ar_event_winning_button
(
    his_ar_event_winning_button_id int auto_increment comment '아이디'
        primary key,
    ori_ar_event_winning_button_id int                                  not null,
    his_web_event_base_id          int                                  null,
    ar_event_winning_id            int                                  null comment 'AR_EVENT_WINNING.id',
    button_action_type             varchar(20)                          null comment '버튼 액션 타입',
    button_text                    varchar(50)                          null comment '버튼 문구',
    button_link_url                varchar(100)                         null comment '버튼 링크 url',
    button_sort                    int                                  null comment '순서',
    delivery_name_yn               tinyint(1) default 1                 null comment '버튼 액션 타입이 경품배송일때 성명 사용여부',
    delivery_phone_number_yn       tinyint(1) default 1                 null comment '버튼 액션 타입이 경품배송일때 전화번호 사용여부',
    delivery_address_yn            tinyint(1) default 0                 null comment '버튼 액션 타입이 경품배송일때 배송주소 사용여부',
    created_by                     varchar(50)                          null comment '생성자',
    created_date                   datetime   default CURRENT_TIMESTAMP null comment '생성일',
    last_modified_by               varchar(50)                          null comment '수정자',
    last_modified_date             datetime                             null comment '수정일'
)
    comment '이벤트 당첨자 버튼 정보' charset = utf8;

create table history_web_event_base
(
    his_web_event_base_id int auto_increment comment '인덱스'
        primary key,
    ori_id                int                                   not null comment '원본 테이블 ID',
    event_id              varchar(7)                            not null comment '이벤트 아이디(5자리부터 시작)',
    event_title           varchar(45)                           null comment '이벤트 타이틀',
    marketing_id          varchar(45)                           null comment '계약 인덱스 값',
    contract_status       varchar(10)                           null comment '계약상태 값',
    event_type            varchar(10)                           null comment '이벤트 종류 타입(AR, ROLLET)',
    event_start_date      date                                  null comment '서비스 시작일',
    event_end_date        date                                  null comment '서비스 종료일',
    real_event_end_date   date                                  null comment '실제 서비스 종료일',
    created_by            varchar(50)                           null comment '생성자',
    created_date          datetime                              null comment '생성일',
    last_modified_by      varchar(50)                           null comment '수정자',
    last_modified_date    datetime                              null comment '수정일',
    copy_date             datetime    default CURRENT_TIMESTAMP null,
    action_type           varchar(10) default 'INSERT'          null,
    qr_code_url           varchar(200)                          null comment 'QR코드 이미지 URL'
)
    comment '히스토리 이벤트 기본 테이블' charset = utf8;

create table event_log_exposure_limit
(
    ar_event_id  int                                  not null,
    code         varchar(30)                          not null,
    status       tinyint(1) default 1                 not null,
    code_desc    varchar(45)                          null,
    created_date datetime   default CURRENT_TIMESTAMP not null,
    constraint code_UNIQUE
        unique (code)
)
    engine = MEMORY;

create index idx_ar_event_id
    on event_log_exposure_limit (ar_event_id);

create table event_log_winning_limit
(
    ar_event_id  int                                  not null,
    code         varchar(30)                          not null,
    status       tinyint(1) default 1                 not null,
    code_desc    varchar(500)                         null,
    created_date datetime   default CURRENT_TIMESTAMP not null,
    constraint code_UNIQUE
        unique (code)
)
    engine = MEMORY;

alter table event_log_exposure
    add created_day_impr int(8) null;

alter table event_log_exposure
    add created_hour_impr int(8) null;

alter table event_log_winning
    add created_day_impr int(8) null comment 'Ex)220901(yymmdd)';

alter table event_log_winning
    add created_hour_impr int(8) null comment 'Ex) 22090105(yymmddhh)';




create index idx_ar_event_id
    on event_log_winning_limit (ar_event_id);

drop index IDX_EVENT_LOG_EXPOSURE on event_log_exposure;
create index IDX_EVENT_LOG_EXPOSURE
    on event_log_exposure (ar_event_id, object_sort, created_day, created_hour, ar_event_object_id);

drop index IDX_EVENT_LOG_WIN on event_log_winning;
create index IDX_EVENT_LOG_WIN
    on event_log_winning (ar_event_id, event_winning_sort, winning_type, created_hour_impr, created_day_impr);

UPDATE event_log_winning A
SET created_day_impr =  DATE_FORMAT(A.created_day, '%y%m%d'),
    created_hour_impr = CONCAT( DATE_FORMAT(A.created_day, '%y%m%d'), A.created_hour)
;

