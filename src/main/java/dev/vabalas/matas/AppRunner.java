package dev.vabalas.matas;

import dev.vabalas.matas.backend.NegateService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.IOException;

@Component
public class AppRunner implements ApplicationRunner {

    private final NegateService negateService;

    public AppRunner(NegateService negateService) {
        this.negateService = negateService;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        negateService.extractInterestingRows(ResourceUtils.getFile("classpath:batch-file.xlsx"));
    }
}
