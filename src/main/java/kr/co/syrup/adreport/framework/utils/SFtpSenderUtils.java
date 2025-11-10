package kr.co.syrup.adreport.framework.utils;

import com.jcraft.jsch.*;
import lombok.Builder;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@NoArgsConstructor
@Slf4j
public class SFtpSenderUtils {

    private String host;
    private Integer port = 21;
    private String username;
    private String password;
    private String defaultPath;

    private Session session;
    private Channel channel;
    private ChannelExec channelExec;
    private ChannelSftp channelSftp;

    private int timeout = 1000 * 10;

    @Builder
    public SFtpSenderUtils(String host, Integer port, String username, String password, String defaultPath){
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.defaultPath = defaultPath;
    }

    public SFtpSenderUtils connect(){
        try {
            log.info("**** SFTP connect start (host:{}, port:{}, id:{}, password:{})", host, port, username, password);
            session = new JSch().getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(timeout);
            log.info("**** SFTP connect ! {}", session.isConnected());
        } catch (JSchException e) {
            log.info("Unable to login to SFTP server(host:{}, port:{}, id:{}, password:{}).", host, port, username, password);
            log.error(e.getMessage(), e);
        } catch (Exception e){
            log.info("SFTP connect Exception");
            log.error(e.getMessage(), e);
        }

        return this;
    }

    public Map<String, Boolean> fileSend(File file) {
        Map<String, Boolean> resultMap = fileSendAll(file);
        return resultMap;
    }

    public Map<String, Boolean> fileSendAll(File... files) {
        Map<String, Boolean> resultMap = new HashMap<String, Boolean>();

        log.info("**** SFTP - fileSendAll start");

//        if (session == null || session.isConnected() == false) {
//            connect();
//        }

        if (session == null || session.isConnected() == false) {
            log.error("**** SFTP - ssh 연결이 되지 않았습니다.");
            Map<String, Boolean> errorMap = new HashMap<String, Boolean>();
            errorMap.put("error", true);
            return  errorMap;
            //return null;
        }

        String localPath = null;
        String remoteFileName = null;

        try {
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            if (!StringUtils.isEmpty(defaultPath)) {
                channelSftp.cd(defaultPath);
            }

            for(File file : files){
                String fileName = file.getName();

                log.info("**** SFTP - fileSend start {}", fileName);

                if (session == null ||  session.isConnected() == false) {
                    log.error("**** SFTP - ssh 연결이 되지 않았습니다.");
                    resultMap.put(fileName, false);
                }

                localPath = file.getPath();

                remoteFileName = fileName;

                log.info("localPath : {}, remoteFileName : {}", localPath, remoteFileName);

                log.info("pwd : {}", pwd());

                FileInputStream in = new FileInputStream(file);
                channelSftp.put(in, remoteFileName);

                if (this.exists(file.getName())) {
                    log.info("**** SFTP - fileName : {} 전송 후 파일 존재 확인.", fileName);
                }

                resultMap.put(fileName, true);
            }
        } catch (JSchException e) {
            log.error(e.getMessage(), e);
        } catch (SftpException e) {
            log.error(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        } finally {
            this.disConnect();
        }

        return resultMap;
    }

    public Map<String, Boolean> fileSendAllList(List<File> fileList) {
        Map<String, Boolean> resultMap = new HashMap<String, Boolean>();

        log.info("**** SFTP - fileSendAll start");

        if (session == null || session.isConnected() == false) {
            log.error("**** SFTP - ssh 연결이 되지 않았습니다.");
            Map<String, Boolean> errorMap = new HashMap<String, Boolean>();
            errorMap.put("error", true);
            return  errorMap;
            //return null;
        }

        String localPath = null;
        String remoteFileName = null;

        try {
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            if (!StringUtils.isEmpty(defaultPath)) {
                channelSftp.cd(defaultPath);
            }

            for(File file : fileList){
                String fileName = file.getName();

                log.info("**** SFTP - fileSend start {}", fileName);

                if (session == null ||  session.isConnected() == false) {
                    log.error("**** SFTP - ssh 연결이 되지 않았습니다.");
                    resultMap.put(fileName, false);
                    break;
                }

                localPath = file.getPath();

                remoteFileName = fileName;

                log.info("localPath : {}, remoteFileName : {}", localPath, remoteFileName);

                log.info("pwd : {}", pwd());

                FileInputStream in = new FileInputStream(file);
                channelSftp.put(in, remoteFileName);

                if (this.exists(file.getName())) {
                    log.info("**** SFTP - fileName : {} 전송 후 파일 존재 확인.", fileName);
                }

                resultMap.put(fileName, true);
            }
        } catch (JSchException e) {
            log.error(e.getMessage(), e);
        } catch (SftpException e) {
            log.error(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        } finally {
            this.disConnect();
        }

        return resultMap;
    }


    public void disConnect() {
        if (session != null) {
            session.disconnect();
        }
        if (channel != null) {
            channel.disconnect();
        }
        if(channelExec != null) {
            channelExec.disconnect();
        }
        if(channelSftp != null) {
            channelSftp.disconnect();
        }

        log.info("**** SFTP disConnect !");
    }

    /**
     * 디렉토리( or 파일) 존재 여부
     * @param path 디렉토리 (or 파일)
     * @return
     */
    public boolean exists(String path) {
        Vector res = null;
        try {
            res = channelSftp.ls(path);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
        }
        return res != null && !res.isEmpty();
    }

    public String pwd() {
        String res = "";
        try {
            res = channelSftp.pwd();
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return res;
            }
        }
        return res;
    }
}
