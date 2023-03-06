import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IContainer } from 'app/shared/model/container.model';
import { getEntities as getContainers } from 'app/entities/container/container.reducer';
import { IParcel } from 'app/shared/model/parcel.model';
import { ParcelSize } from 'app/shared/model/enumerations/parcel-size.model';
import { getEntity, updateEntity, createEntity, reset } from './parcel.reducer';

export const ParcelUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const containers = useAppSelector(state => state.container.entities);
  const parcelEntity = useAppSelector(state => state.parcel.entity);
  const loading = useAppSelector(state => state.parcel.loading);
  const updating = useAppSelector(state => state.parcel.updating);
  const updateSuccess = useAppSelector(state => state.parcel.updateSuccess);
  const parcelSizeValues = Object.keys(ParcelSize);

  const handleClose = () => {
    navigate('/parcel');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getContainers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...parcelEntity,
      ...values,
      container: containers.find(it => it.id.toString() === values.container.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          size: 'SMALL',
          ...parcelEntity,
          container: parcelEntity?.container?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="cassandraSampleApp.parcel.home.createOrEditLabel" data-cy="ParcelCreateUpdateHeading">
            Create or edit a Parcel
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="parcel-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Parcel Number" id="parcel-parcelNumber" name="parcelNumber" data-cy="parcelNumber" type="text" />
              <ValidatedField label="Size" id="parcel-size" name="size" data-cy="size" type="select">
                {parcelSizeValues.map(parcelSize => (
                  <option value={parcelSize} key={parcelSize}>
                    {parcelSize}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField id="parcel-container" name="container" data-cy="container" label="Container" type="select">
                <option value="" key="0" />
                {containers
                  ? containers.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/parcel" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ParcelUpdate;
