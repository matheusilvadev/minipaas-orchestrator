package com.paas.application.port.out;

import java.io.File;

import com.paas.domain.valueobject.RepositoryUrl;

public interface GitClientPort {
    /**
     * Clona um repositório para um diretório local
     * 
     * @param url         A URL do reporitório (VO do domain)
     * @param branch      Branch desejada
     * @param destination O local no server onde o código será baixado
     * @return O diretórioonde o código foi clonado
     */
    File clone(RepositoryUrl url, String branch, File destination);
}
