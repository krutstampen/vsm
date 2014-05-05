//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.05.02 at 10:57:26 AM CEST 
//


package com.cybercom.svp.machine.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assembly-id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="chunk-index" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="chunk-size" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="chunk" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="md5" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "assemblyId",
    "chunkIndex",
    "chunkSize",
    "chunk",
    "md5"
})
@XmlRootElement(name = "firmware-download-response")
public class FirmwareDownloadResponse {

    @XmlElement(name = "assembly-id", required = true)
    protected String assemblyId;
    @XmlElement(name = "chunk-index")
    protected int chunkIndex;
    @XmlElement(name = "chunk-size")
    protected long chunkSize;
    @XmlElement(required = true)
    protected byte[] chunk;
    @XmlElement(required = true)
    protected String md5;

    /**
     * Gets the value of the assemblyId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssemblyId() {
        return assemblyId;
    }

    /**
     * Sets the value of the assemblyId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssemblyId(String value) {
        this.assemblyId = value;
    }

    /**
     * Gets the value of the chunkIndex property.
     * 
     */
    public int getChunkIndex() {
        return chunkIndex;
    }

    /**
     * Sets the value of the chunkIndex property.
     * 
     */
    public void setChunkIndex(int value) {
        this.chunkIndex = value;
    }

    /**
     * Gets the value of the chunkSize property.
     * 
     */
    public long getChunkSize() {
        return chunkSize;
    }

    /**
     * Sets the value of the chunkSize property.
     * 
     */
    public void setChunkSize(long value) {
        this.chunkSize = value;
    }

    /**
     * Gets the value of the chunk property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getChunk() {
        return chunk;
    }

    /**
     * Sets the value of the chunk property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setChunk(byte[] value) {
        this.chunk = value;
    }

    /**
     * Gets the value of the md5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMd5() {
        return md5;
    }

    /**
     * Sets the value of the md5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMd5(String value) {
        this.md5 = value;
    }

}