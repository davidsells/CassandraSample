import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Parcel from './parcel';
import ParcelDetail from './parcel-detail';
import ParcelUpdate from './parcel-update';
import ParcelDeleteDialog from './parcel-delete-dialog';

const ParcelRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Parcel />} />
    <Route path="new" element={<ParcelUpdate />} />
    <Route path=":id">
      <Route index element={<ParcelDetail />} />
      <Route path="edit" element={<ParcelUpdate />} />
      <Route path="delete" element={<ParcelDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ParcelRoutes;
