//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.05.02 at 10:57:25 AM CEST 
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
    "chunkSize"
})
@XmlRootElement(name = "firmware-download-request")
public class FirmwareDownloadRequest {

    @XmlElement(name = "assembly-id", required = true)
    protected String assemblyId;
    @XmlElement(name = "chunk-index")
    protected int chunkIndex;
    @XmlElement(name = "chunk-size")
    protected long chunkSize;

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

}