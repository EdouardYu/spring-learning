package edouard.yu.springsecuritylearning.security;

import edouard.yu.springsecuritylearning.entity.User;
import edouard.yu.springsecuritylearning.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor // génère un constructeur avec les attributs ayant le champ final ou ayant l'annotation @NonNull
@Service
public class JwtService {
    private final UserService userService;
    @Value("${encryption.key}")
    private String ENCRYPTION_KEY;

    // méthode permettant de retourner le token JWT à l'utilisateur quand il fait une requête /signin
    public Map<String, String> generate(String email) {
        User user = this.userService.loadUserByUsername(email);
        // rem : on peut vérifier les informations du token JWT sur : https://jwt.io/
        return this.generateJwt(user);
    }

    // méthode permettant de vérifier si le token JWT a expiré
    public boolean isTokenExpired(String token) {
        Date expirationDate = this.getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    // méthode permettant d'extraire l'email dans les claims du token JWT
    public String extractEmail(String token) {
        return this.getClaim(token, Claims::getSubject);
    }

    // méthode permettant de générer un token JWT pour l'utilisateur
    private Map<String, String> generateJwt(User user) {
        final long currentTime = System.currentTimeMillis(); // l'instant actuel en millisecondes
        final long expirationTime = currentTime + 30 * 60 * 1000; // // l'instant actuel + 30 minutes en millisecondes
        final Map<String, String/*Object*/> claims = Map.of(
                // Par défaut, grâce au builder, on a déjà les trois informations suivantes (en commentaire) dans les claims, donc pas besoin de les renseigner ici
                //Claims.ISSUED_AT, new Date(currentTime),
                //Claims.EXPIRATION, new Date(expirationTime),
                //Claims.SUBJECT, user.getEmail(),
                "username", user.getUsername(),
                "role", user.getRole().getLabel().name()
        );

        final String bearer = Jwts.builder()
                .issuedAt(new Date(currentTime)) // la date de création du token
                .expiration(new Date(expirationTime)) // la date d'expiration du token
                .subject(user.getEmail()) // l'utilisateur pour qui on génère le token
                .claims(claims) // les informations de l'utilisateur
                .signWith(this.getKey()) // la clé de chiffrement du token
                .compact(); // permet de convertir un builder vers un string

        return Map.of("bearer", bearer);
    }

    // méthode permettant de générer la clé de chiffrement/cryptage du token JWT
    private SecretKey getKey() {
        final byte[] decoder = Decoders.BASE64.decode(this.ENCRYPTION_KEY);
        // decode un string qui va se servir de clé secrète,
        // on peut générer une clé depuis : https://randomgenerate.io/encryption-key-generator
        // et qu'on stocke ensuite dans application.properties
        // rem : il y a plusieurs manières de générer une clé et hmacShaKeyFor est l'une d'entre elles
        return Keys.hmacShaKeyFor(decoder);
    }

    // méthode permettant d'extraire la date d'expiration du token JWT depuis ses claims
    private Date getExpirationDateFromToken(String token) {
        return this.getClaim(token, Claims::getExpiration);
    }

    // méthode permettant d'extraire un claim du token JWT
    private <T> T getClaim(String token, Function<Claims, T> function) { // <T> T permet de retourner une valeur de type quelconque
        Claims claims = this.getAllClaims(token);
        return function.apply(claims);
    }

    // méthode permettant d'extraire tous les claims du token JWT
    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(this.getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
