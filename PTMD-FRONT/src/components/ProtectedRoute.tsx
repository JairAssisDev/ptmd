import { Navigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import { CircularProgress, Box } from '@mui/material'

interface ProtectedRouteProps {
  children: React.ReactNode
  requiredRole?: 'ADMIN' | 'MEDICO'
}

const ProtectedRoute = ({ children, requiredRole }: ProtectedRouteProps) => {
  const { user, isAuthenticated, loading } = useAuth()

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="100vh">
        <CircularProgress />
      </Box>
    )
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  if (requiredRole && user?.role !== requiredRole && user?.role !== 'ADMIN') {
    return <Navigate to="/login" replace />
  }

  return <>{children}</>
}

export default ProtectedRoute

