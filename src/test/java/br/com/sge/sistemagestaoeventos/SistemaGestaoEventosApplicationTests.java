package br.com.sge.sistemagestaoeventos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
class SistemaGestaoEventosApplicationTests {

    @Test
    @DisplayName("Deve executar o metodo main sem lancar exececoes")
    void mainTest(){
        assertThatCode( () -> SistemaGestaoEventosApplication.main(new String[]{
                "--spring.main.web-application-type=none"
        })).doesNotThrowAnyException();
    }

}
