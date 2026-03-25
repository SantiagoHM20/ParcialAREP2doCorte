# Parcial Arep

Secuencia de lucas


Sistema distribuido en Spring Boot compuesto por dos aplicaciones: un servicio matematico que calcula la sucesion de Lucas y un proxy que reenvia las solicitudes al servicio principal o al de respaldo en caso de fallo.



### Prerequisitos


```bash
# Java 17
java -version

# Maven 3.9+
mvn -version

# Docker (optional, for deployment)
docker --version
```

### Instalando



```bash
# 1) Clona el repositorio
git clone https://github.com/SantiagoHM20/ParcialAREP2doCorte.git
cd ParcialAREP2doCorte

```

And repeat

```bash
# 2) haz build con maven
cd ParcialProxy o cd ParcialMathService
mvn clean package

```

```bash
# 3) Corre el servicio en una instancia (default 8080)
cd ../preparcialService
mvn spring-boot:run
```

```bash
# 4) corre el servicio en otra instancia (backup on 8081)
cd ../preparcialService
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

```bash
# 5) corre el proxy
cd ../preparcialProxy
set SERVICE1=http://localhost:8080
set SERVICE2=http://localhost:8081
mvn spring-boot:run
```


```bash
# prueba
curl "http://localhost:8080/lucasseq?value=4"

# Respuesta 
{"output":"2, 1, 3, 4","input":"4","operation":"Secuencia de Lucas"}
```


## Despliegue

Para el despliegue se utilizó docker y 3 instancias en aws, una de proxy y 2 de backend

Para ello se contruyó la imagen docker
```bash
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Para el proxy:

docker build -t santiagohm20/proxy-parcial-arep .

docker push santiagohm20/proxy-parcial-arep

Y para el Backend:

docker build -t santiagohm20/parcial-math-service .
docker push santiagohm20/parcial-math-service



### En las instancias aws
```bash
sudo yum install docker -Y

sudo systemctl start docker

sudo docker login

```


#### Backend
```bash
sudo docker run -p 8080:8080 santiagohm20/parcial-math-service
```

Backend 1 (18.205.150.167)
![image](assets/Aplicación%20corriendo%20en%20Backend%2018.205.150.167.png)

Backend 2 (54.89.180.177 )
![image](assets/Aplicación%20corriendo%20en%20Backend%2054.89.180.177.png)

#### Proxy

```bash

sudo docker run -p 8080:8080 -e SERVICE1=http://18.205.150.167:8080 -e SERVICE2=http://54.89.180.177:8080 santiagohm20/proxy-parcial-arep
```

Proxy (3.86.146.183)
![image](assets/Aplicación%20corriendo%20en%20proxy.png)

Note que en el comando docker run del proxy además del contenedor del proxy, se le pasa -e SERVICE1=http://18.205.150.167:8080   y  -e SERVICE2=http://54.89.180.177:8080

Esas son las variables de entorno que el servicio http usa para redirigir hacia esas instancias EC2 mediante Round Robin.

### Prueba del RoundRobin
En ParcialAREP2doCorte\ParcialProxy\src\main\java\co\edu\eci\ParcialProxy\service\HttpService.java:

```java

@Service
public class HttpService {

    private String[] services = {
            System.getenv("SERVICE1"),
            System.getenv("SERVICE2")
    };

    private RestTemplate restTemplate = new RestTemplate();
    private int counter = 0;

    public synchronized List<Long> getResult(String value) {
        String serviceUrl = services[counter];
        counter = (counter + 1) % services.length;

        String url = serviceUrl + "/lucasseq?value=" + value;
        LucasResponse response = restTemplate.getForObject(url, LucasResponse.class);

        return Arrays.stream(response.getOutput().split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}

```
### Controlador del proxy:

```java
@RestController
public class ProxyController {

    private HttpService httpService;
    @Autowired
    public ProxyController(HttpService httpService){
        this.httpService = httpService;
    }

    @GetMapping("/lucasseq")
    public ResponseEntity<?> getLucasSeq(@RequestParam("value") String value){
        List<?> lucasSeq = httpService.getResult(value);

        String output = lucasSeq.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        Map<String, Object> response = new HashMap<>();
        response.put("operation", "Secuencia de Lucas");
        response.put("input", value);
        response.put("output", output);

        return ResponseEntity.ok(response);
    }
}
```

Este controlador está diseñado para devolver la operación del Backend el cual es la lista de Lucas y se le añadió que devolviera el parametro de búsqueda y el nombre de la operación, en el controlador del Backend se hizo similar:

### Controlador del Backend:

```java
@RestController
public class MathController{


    private MathService mathService;

    public MathController(MathService mathService) {
        this.mathService = mathService;
    }


    @GetMapping("/lucasseq")
    public ResponseEntity<?> getLucasSeq(@RequestParam("value") int value){
        List<Integer> lucasSeq = mathService.getLucasSeq(value);

        String output = lucasSeq.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        Map<String, Object> response = new HashMap<>();
        response.put("operation", "Secuencia de Lucas");
        response.put("input", value);
        response.put("output", output);

        return ResponseEntity.ok(response);
    }

}
```

Y finalmente el servicio que calcula la secuencia de Lucas:

### Servicio del Backend(Math-Service):

```java
@Service
public class MathService {

    private List<Integer> lucasSeq;

    public List<Integer> getLucasSeq(int n){
        ArrayList lucasSeq = new ArrayList<>();
        for(int i = 0; i < n; i ++) {
            lucasSeq.add(calculateLucas(i));
        }

        return lucasSeq;
    }

    public Integer calculateLucas(int n){
        if(n <= 0){
            return 2;
        }
        if(n == 1){
            return 1;
        }

        int result = 0;
        result = calculateLucas(n - 1) + calculateLucas(n - 2);
        return result;

    }
}
```
Aseguramos los casos base y mediante recurrencia obtenemos los demás numeros de la secuencia.

### Formulario web
```html
<!DOCTYPE html>
<html>
<head>
    <title>Math Service</title>
</head>
<body>

<h2>Calculadora de secuencia de lucas</h2>

<input id="value" type="number" placeholder="Ingresa n">
<button onclick="calculate()">Calcular Secuencia de Lucas</button>

<p id="result"></p>

<script>
    function calculate() {

        let value = document.getElementById("value").value;

        fetch("/lucasseq?value=" + value)
                .then(response => response.json())
            .then(data => {
                document.getElementById("result").innerHTML = JSON.stringify(data);
            });

    }
</script>

</body>
</html>
```


### Prueba de app corriendo por el proxy(http://3.86.146.183:8080/lucasseq?value=5): 

![img](assets/proxy%20value%205%20no%20web.png)

### Prueba de app corriendo por el proxy mediante el formulario web(http://3.86.146.183:8080) para value = 9: s

![img](assets/proxy%20web.png)

![img](assets/proxy%20web%20value%209.png)

Output de consola:

![img](assets/Output%20de%20consola%20sobre%20el%20proxy%20para%20value%20igual%20a%209.png)



## Construido con

* [Spring Boot](https://spring.io/projects/spring-boot) - Framework for REST services
* [Maven](https://maven.apache.org/) - Dependency Management
* [Docker](https://www.docker.com/) - Containerization and deployment

## Autor

* **Santiago Hurtado** - [SantiagoHM20](https://github.com/SantiagoHM20)

