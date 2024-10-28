package Encryption;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionHelper {
    
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding"; // Ensure it's CBC or another mode that requires an IV
    private static final int IV_SIZE = 16; // 16 bytes IV for AES
    
    private SecretKey secretKey;
    private SecureRandom secureRandom;
    
    public EncryptionHelper() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // AES-256 key
        secretKey = keyGenerator.generateKey();
        secureRandom = new SecureRandom();
    }

    // Generate a random IV
    private byte[] generateIV() {
        byte[] iv = new byte[IV_SIZE];
        secureRandom.nextBytes(iv); // Fill with random bytes
        return iv;
    }

    // Encrypt method using the IV
    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = generateIV(); // Generate a new IV
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv); // Use the IV for encryption
        
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        byte[] encryptedDataWithIV = new byte[IV_SIZE + encryptedData.length];
        
        // Prepend the IV to the encrypted data
        System.arraycopy(iv, 0, encryptedDataWithIV, 0, IV_SIZE);
        System.arraycopy(encryptedData, 0, encryptedDataWithIV, IV_SIZE, encryptedData.length);
        
        return Base64.getEncoder().encodeToString(encryptedDataWithIV);
    }

    // Decrypt method, using the IV prepended to the encrypted data
    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(decodedData, 0, iv, 0, IV_SIZE); // Extract the IV from the data
        
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        
        byte[] originalData = cipher.doFinal(decodedData, IV_SIZE, decodedData.length - IV_SIZE);
        return new String(originalData);
    }
}
