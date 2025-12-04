import { useState, useEffect } from 'react'
import {
  Typography,
  Grid,
  Card,
  CardContent,
  Button,
  Box,
  Alert,
  TextField,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from '@mui/material'
import {
  Dashboard as DashboardIcon,
  Download as DownloadIcon,
  Lock as LockIcon,
} from '@mui/icons-material'
import { adminService, DashboardResponse } from '../services/adminService'

const AdminDashboard = () => {
  const [dashboard, setDashboard] = useState<DashboardResponse | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [changePasswordOpen, setChangePasswordOpen] = useState(false)
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  })
  const [passwordError, setPasswordError] = useState('')
  const [passwordSuccess, setPasswordSuccess] = useState(false)

  useEffect(() => {
    loadDashboard()
  }, [])

  const loadDashboard = async () => {
    try {
      const data = await adminService.getDashboard()
      setDashboard(data)
    } catch (err: any) {
      setError('Erro ao carregar dashboard')
    } finally {
      setLoading(false)
    }
  }

  const handleDownloadBackup = async () => {
    try {
      const blob = await adminService.downloadBackup()
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `ptmd_database_${new Date().toISOString().split('T')[0]}.zip`
      document.body.appendChild(a)
      a.click()
      window.URL.revokeObjectURL(url)
      document.body.removeChild(a)
    } catch (err: any) {
      const errorMessage = err.response?.headers?.['x-error-message'] || 
                          err.response?.data?.error || 
                          'Erro ao gerar database'
      setError(errorMessage)
    }
  }

  const handleChangePassword = async () => {
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      setPasswordError('As senhas não coincidem')
      return
    }

    if (passwordData.newPassword.length < 6) {
      setPasswordError('A senha deve ter no mínimo 6 caracteres')
      return
    }

    try {
      await adminService.changePassword({
        currentPassword: passwordData.currentPassword,
        newPassword: passwordData.newPassword,
      })
      setPasswordSuccess(true)
      setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' })
      setTimeout(() => {
        setChangePasswordOpen(false)
        setPasswordSuccess(false)
      }, 2000)
    } catch (err: any) {
      setPasswordError(err.response?.data?.error || 'Erro ao alterar senha')
    }
  }

  if (loading) {
    return <Typography>Carregando...</Typography>
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Dashboard Administrativo
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>
          {error}
        </Alert>
      )}

      <Grid container spacing={3} sx={{ mt: 2 }}>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" mb={2}>
                <DashboardIcon color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">Total de Imagens</Typography>
              </Box>
              <Typography variant="h3">{dashboard?.totalImages || 0}</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" mb={2}>
                <DashboardIcon color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">Total de Consultas</Typography>
              </Box>
              <Typography variant="h3">{dashboard?.totalConsultations || 0}</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" mb={2}>
                <DashboardIcon color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">Total de Pacientes</Typography>
              </Box>
              <Typography variant="h3">{dashboard?.totalPatients || 0}</Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Box sx={{ mt: 4 }}>
        <Button
          variant="contained"
          startIcon={<DownloadIcon />}
          onClick={handleDownloadBackup}
          sx={{ mr: 2 }}
        >
          Download Database
        </Button>
        <Button
          variant="outlined"
          startIcon={<LockIcon />}
          onClick={() => setChangePasswordOpen(true)}
        >
          Alterar Senha
        </Button>
      </Box>

      <Dialog open={changePasswordOpen} onClose={() => setChangePasswordOpen(false)}>
        <DialogTitle>Alterar Senha</DialogTitle>
        <DialogContent>
          {passwordSuccess && (
            <Alert severity="success" sx={{ mb: 2 }}>
              Senha alterada com sucesso!
            </Alert>
          )}
          {passwordError && (
            <Alert severity="error" sx={{ mb: 2 }} onClose={() => setPasswordError('')}>
              {passwordError}
            </Alert>
          )}
          <TextField
            margin="dense"
            label="Senha Atual"
            type="password"
            fullWidth
            value={passwordData.currentPassword}
            onChange={(e) =>
              setPasswordData({ ...passwordData, currentPassword: e.target.value })
            }
            sx={{ mb: 2 }}
          />
          <TextField
            margin="dense"
            label="Nova Senha"
            type="password"
            fullWidth
            value={passwordData.newPassword}
            onChange={(e) =>
              setPasswordData({ ...passwordData, newPassword: e.target.value })
            }
            sx={{ mb: 2 }}
          />
          <TextField
            margin="dense"
            label="Confirmar Nova Senha"
            type="password"
            fullWidth
            value={passwordData.confirmPassword}
            onChange={(e) =>
              setPasswordData({ ...passwordData, confirmPassword: e.target.value })
            }
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setChangePasswordOpen(false)}>Cancelar</Button>
          <Button onClick={handleChangePassword} variant="contained">
            Alterar
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  )
}

export default AdminDashboard

