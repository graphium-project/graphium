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
package at.srfg.graphium.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.AccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import at.srfg.graphium.api.exceptions.InconsistentStateException;
import at.srfg.graphium.api.exceptions.NotificationException;
import at.srfg.graphium.api.exceptions.ResourceNotFoundException;
import at.srfg.graphium.api.exceptions.ValidationException;
import at.srfg.graphium.core.exception.GraphAlreadyExistException;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.exception.SubscriptionFailedException;
import at.srfg.graphium.io.adapter.exception.XInfoNotSupportedException;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;

/**
 * Created by shennebe on 30.08.2016.
 */
@ControllerAdvice
public class GlobalExceptionController {

    private static Logger log = LoggerFactory
            .getLogger(GlobalExceptionController.class);

    @ResponseStatus(value = HttpStatus.NOT_IMPLEMENTED, reason = "XInfo Not Implemented")
    @ExceptionHandler(XInfoNotSupportedException.class)
    public void handleXInfoNotSupportedException(XInfoNotSupportedException ex) {
        log.warn("This external info is not supported",ex);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
    @ExceptionHandler(ResourceNotFoundException.class)
    public void handleResourceNotException(ResourceNotFoundException ex) {
        log.warn("Requested resource could not be found",ex);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Graph does not exists")
    @ExceptionHandler(GraphNotExistsException.class)
    public void handleGraphNotExistsException(GraphNotExistsException ex) {
        log.warn("Graph does not exists");
    }

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Subscription failed")
    @ExceptionHandler(SubscriptionFailedException.class)
    public void handleSubscriptionFailedException(SubscriptionFailedException ex) {
        log.warn("Subscription failed");
    }

    @ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE, reason = "Mediatype is not acceptable")
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public void handleMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
        log.warn("Requested media type is not acceptable");
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Serialization of waysegment data failed")
    @ExceptionHandler(WaySegmentSerializationException.class)
    public void handleWaySegmentSerializationException(WaySegmentSerializationException e) {
        log.warn("Serialization of waysegment data failed");
    }
    
    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Inconsistent state in Segment Ids")
    @ExceptionHandler(InconsistentStateException.class)
    public void handleInconsistentStateException(InconsistentStateException ex) {
        log.error("Error occured during request",ex);
    }

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Graph already exists")
    @ExceptionHandler(GraphAlreadyExistException.class)
    public void handleGraphAlreadyExistsException(GraphAlreadyExistException ex) {
        log.error("Error occured during request",ex);
    }

    @ResponseStatus(value = HttpStatus.BAD_GATEWAY, reason = "Satellite Graphiums could not be notified")
    @ExceptionHandler(NotificationException.class)
    public void handleNotificationException(NotificationException ex) {
        log.error("Error occured during request",ex);
    }


    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY, reason = "Validation of changed parameter failed")
    @ExceptionHandler(ValidationException.class)
    public void handleValidationException(ValidationException ex) {
        log.warn("Invalid request");
    }


    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Access to unknown or restricted field change requested")
    @ExceptionHandler(AccessException.class)
    public void handleAccessException(AccessException ex) {
        log.error("Access to unknown or restricted field requested");
    }


    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleInconsistentStateException(Exception ex) {
        log.error("Error occured during request",ex);
        return this.adaptException2MAV(ex);
    }

    private ModelAndView adaptException2MAV(Exception ex) {
        ModelAndView mav = new ModelAndView();
        mav.setView(new MappingJackson2JsonView());
        mav.addObject("exception",ex.getClass().getSimpleName());
        mav.addObject("message", ex.getMessage());
        return mav;
    }
}
