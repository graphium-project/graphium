/**
 * Copyright © 2017 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.srfg.graphium.gipimport.model.impl;

import java.nio.charset.Charset;
import java.util.Date;

import at.srfg.graphium.gipimport.model.IDFMetadata;

/**
 * Created by shennebe on 11.08.2015.
 */
public class IDFMetadataImpl implements IDFMetadata {

    private String fileName;
    private Date modificationDate;
    private String username;
    private String cpt;
    private String exe;
    private int lib;
    private Charset charset;
    private String dataSet;
    private String dbName;
    private int uid;
    private int cid;
    private char typ;

    public IDFMetadataImpl(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }


    @Override
    public Date getModificationDate() {
        return modificationDate;
    }

    @Override
    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getCpt() {
        return cpt;
    }

    @Override
    public void setCpt(String cpt) {
        this.cpt = cpt;
    }

    @Override
    public String getExe() {
        return exe;
    }

    @Override
    public void setExe(String exe) {
        this.exe = exe;
    }

    @Override
    public int getLib() {
        return lib;
    }

    @Override
    public void setLib(int lib) {
        this.lib = lib;
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    @Override
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override
    public String getDataSet() {
        return dataSet;
    }

    @Override
    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public String getDbName() {
        return dbName;
    }

    @Override
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public int getUid() {
        return uid;
    }

    @Override
    public void setUid(int uid) {
        this.uid = uid;
    }

    @Override
    public int getCid() {
        return cid;
    }

    @Override
    public void setCid(int cid) {
        this.cid = cid;
    }

    @Override
    public char getTyp() {
        return typ;
    }

    @Override
    public void setTyp(char typ) {
        this.typ = typ;
    }

    @Override
    public String toString() {
        return "IDFMetadata{" +
                "fileName=" + fileName +
                ", modificationDate=" + modificationDate +
                ", username=" + username +
                ", cpt=" + cpt +
                ", exe=" + exe +
                ", lib=" + lib +
                ", charset=" + charset +
                ", dataSet=" + dataSet +
                ", dbName=" + dbName +
                ", uid=" + uid +
                ", cid=" + cid +
                ", typ=" + typ +
                '}';
    }
}
