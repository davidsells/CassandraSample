import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './container.reducer';

export const ContainerDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const containerEntity = useAppSelector(state => state.container.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="containerDetailsHeading">Container</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{containerEntity.id}</dd>
          <dt>
            <span id="containerNumber">Container Number</span>
          </dt>
          <dd>{containerEntity.containerNumber}</dd>
          <dt>
            <span id="size">Size</span>
          </dt>
          <dd>{containerEntity.size}</dd>
        </dl>
        <Button tag={Link} to="/container" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/container/${containerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default ContainerDetail;
