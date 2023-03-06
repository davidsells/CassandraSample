import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IContainer } from 'app/shared/model/container.model';
import { ContainerSize } from 'app/shared/model/enumerations/container-size.model';
import { getEntity, updateEntity, createEntity, reset } from './container.reducer';

export const ContainerUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const containerEntity = useAppSelector(state => state.container.entity);
  const loading = useAppSelector(state => state.container.loading);
  const updating = useAppSelector(state => state.container.updating);
  const updateSuccess = useAppSelector(state => state.container.updateSuccess);
  const containerSizeValues = Object.keys(ContainerSize);

  const handleClose = () => {
    navigate('/container');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...containerEntity,
      ...values,
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
          ...containerEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="cassandraSampleApp.container.home.createOrEditLabel" data-cy="ContainerCreateUpdateHeading">
            Create or edit a Container
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="container-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Container Number"
                id="container-containerNumber"
                name="containerNumber"
                data-cy="containerNumber"
                type="text"
              />
              <ValidatedField label="Size" id="container-size" name="size" data-cy="size" type="select">
                {containerSizeValues.map(containerSize => (
                  <option value={containerSize} key={containerSize}>
                    {containerSize}
                  </option>
                ))}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/container" replace color="info">
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

export default ContainerUpdate;
