import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './parcel.reducer';

export const ParcelDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const parcelEntity = useAppSelector(state => state.parcel.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="parcelDetailsHeading">Parcel</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{parcelEntity.id}</dd>
          <dt>
            <span id="parcelNumber">Parcel Number</span>
          </dt>
          <dd>{parcelEntity.parcelNumber}</dd>
          <dt>
            <span id="size">Size</span>
          </dt>
          <dd>{parcelEntity.size}</dd>
          <dt>Container</dt>
          <dd>{parcelEntity.container ? parcelEntity.container.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/parcel" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/parcel/${parcelEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ParcelDetail;
