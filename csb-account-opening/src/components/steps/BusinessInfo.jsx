import { useState } from 'react'

const US_STATES = [
  'AL','AK','AZ','AR','CA','CO','CT','DE','FL','GA','HI','ID','IL','IN','IA',
  'KS','KY','LA','ME','MD','MA','MI','MN','MS','MO','MT','NE','NV','NH','NJ',
  'NM','NY','NC','ND','OH','OK','OR','PA','RI','SC','SD','TN','TX','UT','VT',
  'VA','WA','WV','WI','WY','DC'
]

const BUSINESS_TYPES = [
  { value: '', label: 'Select business type...' },
  { value: 'sole-prop', label: 'Sole Proprietorship' },
  { value: 'partnership', label: 'General Partnership' },
  { value: 'llp', label: 'Limited Liability Partnership (LLP)' },
  { value: 'llc-single', label: 'LLC – Single Member' },
  { value: 'llc-multi', label: 'LLC – Multi Member' },
  { value: 'corp-s', label: 'Corporation (S-Corp)' },
  { value: 'corp-c', label: 'Corporation (C-Corp)' },
  { value: 'nonprofit', label: 'Non-Profit Organization' },
  { value: 'trust', label: 'Trust' },
  { value: 'gov', label: 'Government Entity' },
  { value: 'other', label: 'Other' },
]

function Field({ label, required, error, children }) {
  return (
    <div>
      <label className="form-label">
        {label}
        {required && <span className="required-star ml-1">*</span>}
      </label>
      {children}
      {error && <p className="error-text">{error}</p>}
    </div>
  )
}

export default function BusinessInfo({ formData, updateFormData, onNext, onBack }) {
  const [errors, setErrors] = useState({})

  const validate = () => {
    const e = {}
    if (!formData.businessName.trim()) e.businessName = 'Business name is required.'
    if (!formData.businessType) e.businessType = 'Please select a business type.'
    if (!formData.ein.trim()) e.ein = 'EIN / Tax ID is required.'
    else if (!/^\d{2}-\d{7}$/.test(formData.ein) && !/^\d{9}$/.test(formData.ein.replace(/-/g, '')))
      e.ein = 'Enter a valid EIN (e.g., 12-3456789).'
    if (!formData.stateOfFormation) e.stateOfFormation = 'State of formation is required.'
    if (!formData.dateEstablished) e.dateEstablished = 'Date established is required.'
    if (!formData.businessPhone.trim()) e.businessPhone = 'Business phone is required.'
    if (!formData.businessAddress.trim()) e.businessAddress = 'Street address is required.'
    if (!formData.businessCity.trim()) e.businessCity = 'City is required.'
    if (!formData.businessState) e.businessState = 'State is required.'
    if (!formData.businessZip.trim()) e.businessZip = 'ZIP code is required.'
    else if (!/^\d{5}(-\d{4})?$/.test(formData.businessZip)) e.businessZip = 'Enter a valid ZIP code.'
    if (!formData.businessDescription.trim()) e.businessDescription = 'Please provide a brief description of your business.'
    return e
  }

  const handleNext = () => {
    const e = validate()
    setErrors(e)
    if (Object.keys(e).length === 0) onNext()
  }

  const set = (field) => (e) => updateFormData({ [field]: e.target.value })

  const formatEIN = (val) => {
    const digits = val.replace(/\D/g, '').slice(0, 9)
    if (digits.length >= 3) return digits.slice(0, 2) + '-' + digits.slice(2)
    return digits
  }

  const formatPhone = (val) => {
    const digits = val.replace(/\D/g, '').slice(0, 10)
    if (digits.length >= 7) return `(${digits.slice(0,3)}) ${digits.slice(3,6)}-${digits.slice(6)}`
    if (digits.length >= 4) return `(${digits.slice(0,3)}) ${digits.slice(3)}`
    if (digits.length >= 1) return `(${digits}`
    return digits
  }

  return (
    <div className="space-y-6">
      <div className="card">
        <h2 className="section-header">Business Information</h2>
        <p className="text-gray-500 text-sm">
          Tell us about your business. All fields marked with <span className="required-star">*</span> are required.
        </p>
      </div>

      {/* Business Identity */}
      <div className="card space-y-5">
        <h3 className="font-bold text-gray-700 border-b pb-2">Business Identity</h3>

        <Field label="Legal Business Name" required error={errors.businessName}>
          <input
            type="text"
            className="form-input"
            placeholder="e.g., Acme Widget Company LLC"
            value={formData.businessName}
            onChange={set('businessName')}
          />
        </Field>

        <Field label="DBA / Trade Name" error={errors.dba}>
          <input
            type="text"
            className="form-input"
            placeholder="Doing Business As (if different from legal name)"
            value={formData.dba}
            onChange={set('dba')}
          />
        </Field>

        <div className="grid sm:grid-cols-2 gap-5">
          <Field label="Business Type / Entity Structure" required error={errors.businessType}>
            <select
              className="form-input"
              value={formData.businessType}
              onChange={set('businessType')}
            >
              {BUSINESS_TYPES.map(bt => (
                <option key={bt.value} value={bt.value}>{bt.label}</option>
              ))}
            </select>
          </Field>

          <Field label="State of Formation" required error={errors.stateOfFormation}>
            <select
              className="form-input"
              value={formData.stateOfFormation}
              onChange={set('stateOfFormation')}
            >
              <option value="">Select state...</option>
              {US_STATES.map(s => <option key={s} value={s}>{s}</option>)}
            </select>
          </Field>
        </div>

        <div className="grid sm:grid-cols-2 gap-5">
          <Field label="EIN / Employer Identification Number" required error={errors.ein}>
            <input
              type="text"
              className="form-input"
              placeholder="XX-XXXXXXX"
              value={formData.ein}
              onChange={e => updateFormData({ ein: formatEIN(e.target.value) })}
            />
            <p className="text-xs text-gray-400 mt-1">Format: 12-3456789</p>
          </Field>

          <Field label="Date Established" required error={errors.dateEstablished}>
            <input
              type="date"
              className="form-input"
              value={formData.dateEstablished}
              onChange={set('dateEstablished')}
              max={new Date().toISOString().split('T')[0]}
            />
          </Field>
        </div>

        <Field label="Business Phone" required error={errors.businessPhone}>
          <input
            type="tel"
            className="form-input"
            placeholder="(617) 555-1234"
            value={formData.businessPhone}
            onChange={e => updateFormData({ businessPhone: formatPhone(e.target.value) })}
          />
        </Field>

        <Field label="NAICS Code (Optional)" error={errors.naics}>
          <input
            type="text"
            className="form-input"
            placeholder="e.g., 541511 – Custom Computer Programming"
            value={formData.naics}
            onChange={set('naics')}
          />
          <p className="text-xs text-gray-400 mt-1">
            <a href="https://www.census.gov/naics/" target="_blank" rel="noopener noreferrer" className="underline">
              Look up your NAICS code
            </a>
          </p>
        </Field>

        <Field label="Brief Business Description" required error={errors.businessDescription}>
          <textarea
            className="form-input"
            rows={3}
            placeholder="Describe your primary business activities and products/services offered..."
            value={formData.businessDescription}
            onChange={set('businessDescription')}
          />
        </Field>
      </div>

      {/* Business Address */}
      <div className="card space-y-5">
        <h3 className="font-bold text-gray-700 border-b pb-2">Business Address</h3>
        <div className="info-box">
          <strong>Important:</strong> This must be a physical address (no P.O. boxes). We may send correspondence here.
        </div>

        <Field label="Street Address" required error={errors.businessAddress}>
          <input
            type="text"
            className="form-input"
            placeholder="123 Main Street, Suite 100"
            value={formData.businessAddress}
            onChange={set('businessAddress')}
          />
        </Field>

        <div className="grid sm:grid-cols-3 gap-5">
          <Field label="City" required error={errors.businessCity}>
            <input
              type="text"
              className="form-input"
              placeholder="Cambridge"
              value={formData.businessCity}
              onChange={set('businessCity')}
            />
          </Field>
          <Field label="State" required error={errors.businessState}>
            <select
              className="form-input"
              value={formData.businessState}
              onChange={set('businessState')}
            >
              <option value="">State</option>
              {US_STATES.map(s => <option key={s} value={s}>{s}</option>)}
            </select>
          </Field>
          <Field label="ZIP Code" required error={errors.businessZip}>
            <input
              type="text"
              className="form-input"
              placeholder="02138"
              value={formData.businessZip}
              onChange={set('businessZip')}
              maxLength={10}
            />
          </Field>
        </div>
      </div>

      <div className="flex justify-between gap-3">
        <button className="btn-secondary" onClick={onBack}>← Back</button>
        <button className="btn-primary" onClick={handleNext}>Continue →</button>
      </div>
    </div>
  )
}
