import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  Grid,
} from '@mui/material'
import { authService, RegisterRequest } from '../services/authService'

const Register = () => {
  const navigate = useNavigate()
  const [formData, setFormData] = useState<RegisterRequest>({
    nome: '',
    cpf: '',
    crm: '',
    dataNascimento: '',
    email: '',
    password: '',
  })
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const [loading, setLoading] = useState(false)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    })
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      await authService.register(formData)
      setSuccess(true)
      setTimeout(() => {
        navigate('/login')
      }, 2000)
    } catch (err: any) {
      setError(err.response?.data?.error || 'Erro ao cadastrar médico')
    } finally {
      setLoading(false)
    }
  }

  return (
    <Container component="main" maxWidth="sm">
      <Box
        sx={{
          marginTop: 4,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
        }}
      >
        <Paper elevation={3} sx={{ p: 4, width: '100%' }}>
          <Typography component="h1" variant="h5" align="center" gutterBottom>
            Cadastro de Médico
          </Typography>
          {success && (
            <Alert severity="success" sx={{ mb: 2 }}>
              Médico cadastrado com sucesso! Redirecionando...
            </Alert>
          )}
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}
          <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  required
                  fullWidth
                  id="nome"
                  label="Nome Completo"
                  name="nome"
                  value={formData.nome}
                  onChange={handleChange}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  required
                  fullWidth
                  id="cpf"
                  label="CPF"
                  name="cpf"
                  value={formData.cpf}
                  onChange={handleChange}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  required
                  fullWidth
                  id="crm"
                  label="CRM"
                  name="crm"
                  value={formData.crm}
                  onChange={handleChange}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  id="dataNascimento"
                  label="Data de Nascimento"
                  name="dataNascimento"
                  type="date"
                  InputLabelProps={{
                    shrink: true,
                  }}
                  value={formData.dataNascimento}
                  onChange={handleChange}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  required
                  fullWidth
                  id="email"
                  label="Email"
                  name="email"
                  type="email"
                  value={formData.email}
                  onChange={handleChange}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  required
                  fullWidth
                  name="password"
                  label="Senha"
                  type="password"
                  id="password"
                  value={formData.password}
                  onChange={handleChange}
                />
              </Grid>
            </Grid>
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
              disabled={loading}
            >
              {loading ? 'Cadastrando...' : 'Cadastrar'}
            </Button>
            <Button fullWidth variant="text" onClick={() => navigate('/login')}>
              Já tem conta? Faça login
            </Button>
          </Box>
        </Paper>
      </Box>
    </Container>
  )
}

export default Register

