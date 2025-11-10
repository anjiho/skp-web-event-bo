package kr.co.syrup.adreport.framework.utils;

import com.penta.scpdb.ScpDbAgent;
import kr.co.syrup.adreport.framework.config.properties.ProfileProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


@Slf4j
@Component
public class AES256Utils {

    private final String alg = "AES/CBC/PKCS5Padding";

    @Value("${aes.keyvalue}")
    private String ymlKeyValue;

    @Value("${damo.service.key}")
    private String damoServiceKey;

    @Value("${damo.initFilePath}")
    private String damoInitFilePath;

    public String encrypt(String text) {
        try {
            Cipher cipher = Cipher.getInstance(alg);
            String aesKey = getAesKeyValue();
            SecretKeySpec keySpec = new SecretKeySpec(aesKey.substring(0, 16).getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(aesKey.substring(0, 16).getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

            byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));

            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return text;
    }

    public String decrypt(String cipherText) {
        try {
            Cipher cipher = Cipher.getInstance(alg);
            String aesKey = getAesKeyValue();
            SecretKeySpec keySpec = new SecretKeySpec(aesKey.substring(0, 16).getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(aesKey.substring(0, 16).getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return cipherText;
    }

    public String encrypt(String text, String key) {
        try {
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(key.substring(0,16).getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(key.substring(0,16).getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

            byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));

            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return text;
    }

    public String decrypt(String cipherText, String key) {
        try {
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(key.substring(0,16).getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(key.substring(0,16).getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return cipherText;
    }

    private String getAesKeyValue() {
        String keyValue = "";
        //로컬, 개발은 yml 파일의 16진수의 키를 가져온다
        if (ProfileProperties.isLocal() || ProfileProperties.isDev()) {
            keyValue = ymlKeyValue;
        }
        //알파, 상용은 D'amo 솔루션을 사용하여 16진수 키를 가져온다
        if (ProfileProperties.isAlp() || ProfileProperties.isProd()) {
            try {
                keyValue = this.getDamoKeyValue();
            } catch (Exception e) {
                e.getMessage();
            }
        }
        return StringTools.convertHexToString(keyValue);
    }

    /**
     * D'amo 솔루션의 사용하여 16진수의 암호화 키값을 가져오기
     * @return
     */
    private String getDamoKeyValue() {
        String outKey = "";
        /* DAMO SCP : Create ScpDbAgent object */
        ScpDbAgent agt = new ScpDbAgent();
        outKey = agt.ScpExportKey(damoInitFilePath, damoServiceKey, "");
        return outKey;
    }

    public static void main(String[] args) {
        String str = "01062585228";
        AES256Utils aes256Utils = new AES256Utils();
        String encStr = aes256Utils.encrypt(str);
        log.info("encStr >" + encStr);
        String decStr = aes256Utils.decrypt(encStr);
        log.info("decStr >" + decStr);
    }
}